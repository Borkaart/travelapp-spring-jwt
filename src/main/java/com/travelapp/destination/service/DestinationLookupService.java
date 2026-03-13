package com.travelapp.destination.service;

import com.travelapp.destination.dto.DestinationCityResponse;
import com.travelapp.destination.dto.DestinationCountryResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DestinationLookupService {

    private static final Logger logger = LoggerFactory.getLogger(DestinationLookupService.class);

    private static final int DEFAULT_LIMIT = 10;
    private static final String REST_COUNTRIES_BASE_URL = "https://restcountries.com";
    private static final String PHOTON_API_BASE_URL = "https://photon.komoot.io";

    private final AmadeusClientService amadeusClientService;

    // Simple in-memory cache to avoid hitting RestCountries API on every request
    private List<DestinationCountryResponse> cachedCountries = null;
    private long lastCacheTime = 0;
    private static final long CACHE_DURATION_MS = 24 * 60 * 60 * 1000; // 24 hours

    public List<DestinationCountryResponse> searchCountries(String query) {
        // First check: if cache is empty or expired, try to refresh
        if (cachedCountries == null || (System.currentTimeMillis() - lastCacheTime > CACHE_DURATION_MS)) {
            refreshCountriesCache();
        }

        // If still empty (API down + no previous cache), return empty list
        if (cachedCountries == null || cachedCountries.isEmpty()) {
             logger.warn("Country cache is empty even after refresh attempt");
             return Collections.emptyList();
        }

        String normalizedQuery = normalize(query);

        return cachedCountries.stream()
                .filter(country -> !StringUtils.hasText(normalizedQuery)
                        || normalize(country.getName()).contains(normalizedQuery))
                .toList();
    }

    private synchronized void refreshCountriesCache() {
        if (cachedCountries != null && (System.currentTimeMillis() - lastCacheTime < CACHE_DURATION_MS)) {
            return; // Double-checked locking optimization
        }
        
        try {
            logger.info("Refreshing countries cache from {}", REST_COUNTRIES_BASE_URL);
            RestCountriesCountryResponse[] countries = RestClient.builder()
                    .baseUrl(REST_COUNTRIES_BASE_URL)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v3.1/all")
                            .queryParam("fields", "name,cca2")
                            .build())
                    .retrieve()
                    .body(RestCountriesCountryResponse[].class);

            if (countries != null && countries.length > 0) {
                cachedCountries = Arrays.stream(countries)
                        .filter(Objects::nonNull)
                        .filter(country -> country.name() != null && StringUtils.hasText(country.name().common()))
                        .map(country -> DestinationCountryResponse.builder()
                                .code(country.cca2())
                                .name(country.name().common())
                                .build())
                        .sorted(Comparator.comparing(DestinationCountryResponse::getName, String.CASE_INSENSITIVE_ORDER))
                        .toList();
                
                lastCacheTime = System.currentTimeMillis();
                logger.info("Countries cache refreshed with {} entries", cachedCountries.size());
            } else {
                logger.warn("RestCountries API returned empty response, using fallback");
                useFallbackCountries();
            }
        } catch (Exception ex) {
             logger.error("Failed to refresh countries cache, using fallback", ex);
             useFallbackCountries();
        }
    }

    private void useFallbackCountries() {
        if (cachedCountries != null && !cachedCountries.isEmpty()) {
            return; // Keep existing cache if we have it
        }
        
        logger.info("Initializing fallback countries list");
        cachedCountries = List.of(
            DestinationCountryResponse.builder().code("BR").name("Brazil").build(),
            DestinationCountryResponse.builder().code("US").name("United States").build(),
            DestinationCountryResponse.builder().code("FR").name("France").build(),
            DestinationCountryResponse.builder().code("IT").name("Italy").build(),
            DestinationCountryResponse.builder().code("ES").name("Spain").build(),
            DestinationCountryResponse.builder().code("PT").name("Portugal").build(),
            DestinationCountryResponse.builder().code("GB").name("United Kingdom").build(),
            DestinationCountryResponse.builder().code("DE").name("Germany").build(),
            DestinationCountryResponse.builder().code("JP").name("Japan").build(),
            DestinationCountryResponse.builder().code("CA").name("Canada").build(),
            DestinationCountryResponse.builder().code("AR").name("Argentina").build(),
            DestinationCountryResponse.builder().code("MX").name("Mexico").build()
        ).stream()
        .sorted(Comparator.comparing(DestinationCountryResponse::getName))
        .toList();
        
        // Don't update lastCacheTime so we try again soon
    }

    public List<DestinationCityResponse> searchCities(String countryCode, String query) {
        if (!StringUtils.hasText(query)) {
            logger.debug("Query is empty, returning empty list for city search");
            return Collections.emptyList();
        }

        // 1. Try Amadeus if configured
        if (amadeusClientService.isConfigured()) {
            String token = amadeusClientService.fetchAccessToken();
            if (StringUtils.hasText(token)) {
                try {
                    logger.debug("Searching cities via Amadeus with query: '{}' and countryCode: '{}'", query, countryCode);
                    AmadeusLocationResponse response = amadeusClientService.restClient().get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/v1/reference-data/locations")
                                    .queryParam("subType", "CITY")
                                    .queryParam("keyword", query.trim())
                                    .queryParam("view", "LIGHT")
                                    .build())
                            .header("Authorization", "Bearer " + token)
                            .retrieve()
                            .body(AmadeusLocationResponse.class);

                    if (response != null && response.data() != null && !response.data().isEmpty()) {
                        List<DestinationCityResponse> results = response.data().stream()
                                .filter(Objects::nonNull)
                                .filter(loc -> loc.address() != null)
                                .filter(loc -> !StringUtils.hasText(countryCode) 
                                        || (loc.address().countryCode() != null && loc.address().countryCode().equalsIgnoreCase(countryCode.trim())))
                                .map(this::toCityResponse)
                                .toList();
                        
                        if (!results.isEmpty()) {
                            logger.debug("Found {} cities for query '{}' via Amadeus", results.size(), query);
                            return results;
                        }
                    }
                } catch (Exception ex) {
                    logger.warn("Amadeus city search failed, falling back to Photon: {}", ex.getMessage());
                }
            }
        }

        // 2. Try Photon (OpenStreetMap) - Free and no key required
        try {
            logger.debug("Searching cities via Photon with query: '{}' and countryCode: '{}'", query, countryCode);
            Map<String, Object> response = RestClient.builder()
                    .baseUrl(PHOTON_API_BASE_URL)
                    .build()
                    .get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/api")
                                .queryParam("q", query.trim())
                                .queryParam("type", "city")
                                .queryParam("limit", 10);
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.get("features") instanceof List<?> features) {
                List<DestinationCityResponse> results = features.stream()
                        .filter(f -> f instanceof Map)
                        .map(f -> (Map<String, Object>) f)
                        .map(f -> {
                            Map<String, Object> props = (Map<String, Object>) f.get("properties");
                            Map<String, Object> geom = (Map<String, Object>) f.get("geometry");
                            List<Double> coords = (List<Double>) geom.get("coordinates");
                            
                            String name = (String) props.getOrDefault("name", props.get("city"));
                            String country = (String) props.get("country");
                            String code = (String) props.get("countrycode");
                            
                            if (!StringUtils.hasText(name) || !StringUtils.hasText(country)) return null;
                            
                            // If countryCode filter provided, apply it
                            if (StringUtils.hasText(countryCode) && !countryCode.equalsIgnoreCase(code)) {
                                return null;
                            }

                            return DestinationCityResponse.builder()
                                    .name(name)
                                    .country(country)
                                    .countryCode(code)
                                    .formatted(name + ", " + country)
                                    .lon(coords != null && coords.size() > 0 ? coords.get(0) : null)
                                    .lat(coords != null && coords.size() > 1 ? coords.get(1) : null)
                                    .build();
                        })
                        .filter(Objects::nonNull)
                        .toList();

                if (!results.isEmpty()) {
                    logger.debug("Found {} cities for query '{}' via Photon", results.size(), query);
                    return results;
                }
            }
        } catch (Exception ex) {
            logger.error("Photon city search failed", ex);
        }

        // 3. Last fallback: My hardcoded list
        return getFallbackCities(countryCode, query);
    }

    private List<DestinationCityResponse> getFallbackCities(String countryCode, String query) {
        if (!StringUtils.hasText(query) || query.length() < 2) {
            return Collections.emptyList();
        }
        
        String normQuery = normalize(query);
        
        // A simple list of major cities for common countries as fallback
        List<DestinationCityResponse> fallbacks = List.of(
            // Brazil
            createCityFallback("São Paulo", "Brazil", "BR", -23.5505, -46.6333),
            createCityFallback("Rio de Janeiro", "Brazil", "BR", -22.9068, -43.1729),
            createCityFallback("Brasília", "Brazil", "BR", -15.7801, -47.9292),
            // France
            createCityFallback("Paris", "France", "FR", 48.8566, 2.3522),
            createCityFallback("Lyon", "France", "FR", 45.7640, 4.8357),
            createCityFallback("Marseille", "France", "FR", 43.2965, 5.3698),
            // USA
            createCityFallback("New York", "United States", "US", 40.7128, -74.0060),
            createCityFallback("Los Angeles", "United States", "US", 34.0522, -118.2437),
            createCityFallback("Miami", "United States", "US", 25.7617, -80.1918),
            // UK
            createCityFallback("London", "United Kingdom", "GB", 51.5074, -0.1278),
            // Portugal
            createCityFallback("Lisbon", "Portugal", "PT", 38.7223, -9.1393),
            createCityFallback("Porto", "Portugal", "PT", 41.1579, -8.6291),
            // Italy
            createCityFallback("Rome", "Italy", "IT", 41.9028, 12.4964),
            createCityFallback("Milan", "Italy", "IT", 45.4642, 9.1900)
        );
        
        return fallbacks.stream()
                .filter(city -> !StringUtils.hasText(countryCode) || city.getCountryCode().equalsIgnoreCase(countryCode))
                .filter(city -> normalize(city.getName()).contains(normQuery))
                .toList();
    }

    private DestinationCityResponse createCityFallback(String name, String country, String code, double lat, double lon) {
        return DestinationCityResponse.builder()
                .name(name)
                .country(country)
                .countryCode(code)
                .formatted(name + ", " + country)
                .lat(lat)
                .lon(lon)
                .build();
    }

    public GeoPoint findCityCoordinates(String cityName, String countryName) {
        if (!StringUtils.hasText(cityName)) {
            return null;
        }

        // 1. Try Amadeus
        if (amadeusClientService.isConfigured()) {
            String token = amadeusClientService.fetchAccessToken();
            if (StringUtils.hasText(token)) {
                try {
                    logger.debug("Finding coordinates for city: '{}' via Amadeus", cityName);
                    AmadeusLocationResponse response = amadeusClientService.restClient().get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/v1/reference-data/locations")
                                    .queryParam("subType", "CITY")
                                    .queryParam("keyword", cityName.trim())
                                    .build())
                            .header("Authorization", "Bearer " + token)
                            .retrieve()
                            .body(AmadeusLocationResponse.class);

                    if (response != null && response.data() != null && !response.data().isEmpty()) {
                        return response.data().stream()
                                .filter(loc -> loc.geoCode() != null)
                                .filter(loc -> !StringUtils.hasText(countryName) 
                                        || (loc.address() != null && loc.address().countryName() != null && loc.address().countryName().equalsIgnoreCase(countryName.trim())))
                                .findFirst()
                                .map(loc -> new GeoPoint(loc.geoCode().latitude(), loc.geoCode().longitude()))
                                .orElse(null);
                    }
                } catch (Exception ex) {
                    logger.warn("Amadeus coordinates search failed: {}", ex.getMessage());
                }
            }
        }

        // 2. Try Photon as fallback for coordinates
        try {
            logger.debug("Finding coordinates for city: '{}' via Photon", cityName);
            Map<String, Object> response = RestClient.builder()
                    .baseUrl(PHOTON_API_BASE_URL)
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api")
                            .queryParam("q", cityName.trim())
                            .queryParam("type", "city")
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.get("features") instanceof List<?> features && !features.isEmpty()) {
                Map<String, Object> feature = (Map<String, Object>) features.get(0);
                Map<String, Object> geom = (Map<String, Object>) feature.get("geometry");
                List<Double> coords = (List<Double>) geom.get("coordinates");
                if (coords != null && coords.size() >= 2) {
                    return new GeoPoint(coords.get(1), coords.get(0)); // Photon is [lon, lat]
                }
            }
        } catch (Exception ex) {
            logger.error("Photon coordinates search failed", ex);
        }

        return null;
    }

    private DestinationCityResponse toCityResponse(AmadeusLocation location) {
        return DestinationCityResponse.builder()
                .name(location.name())
                .country(location.address().countryName())
                .countryCode(location.address().countryCode())
                .formatted(location.name() + ", " + location.address().countryName())
                .lat(location.geoCode() != null ? location.geoCode().latitude() : null)
                .lon(location.geoCode() != null ? location.geoCode().longitude() : null)
                .build();
    }

    private String buildCityKey(DestinationCityResponse city) {
        return normalize(city.getName()) + "|" + normalize(city.getCountry());
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
