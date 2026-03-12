package com.travelapp.destination.service;

import com.travelapp.destination.dto.DestinationCityResponse;
import com.travelapp.destination.dto.DestinationCountryResponse;
import lombok.RequiredArgsConstructor;
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

    private static final int DEFAULT_LIMIT = 10;
    private static final String REST_COUNTRIES_BASE_URL = "https://restcountries.com";

    private final AmadeusClientService amadeusClientService;

    public List<DestinationCountryResponse> searchCountries(String query) {
        try {
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

            if (countries == null) {
                return Collections.emptyList();
            }

            String normalizedQuery = normalize(query);

            return Arrays.stream(countries)
                    .filter(Objects::nonNull)
                    .filter(country -> country.name() != null && StringUtils.hasText(country.name().common()))
                    .filter(country -> !StringUtils.hasText(normalizedQuery)
                            || normalize(country.name().common()).contains(normalizedQuery))
                    .map(country -> DestinationCountryResponse.builder()
                            .code(country.cca2())
                            .name(country.name().common())
                            .build())
                    .sorted(Comparator.comparing(DestinationCountryResponse::getName, String.CASE_INSENSITIVE_ORDER))
                    .toList();
        } catch (RuntimeException ex) {
            return Collections.emptyList();
        }
    }

    public List<DestinationCityResponse> searchCities(String countryCode, String query) {
        if (!amadeusClientService.isConfigured()) {
            return Collections.emptyList();
        }
        if (!StringUtils.hasText(query)) {
            return Collections.emptyList();
        }

        String token = amadeusClientService.fetchAccessToken();
        if (!StringUtils.hasText(token)) {
            return Collections.emptyList();
        }

        try {
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

            if (response == null || response.data() == null) {
                return Collections.emptyList();
            }

            return response.data().stream()
                    .filter(Objects::nonNull)
                    .filter(loc -> loc.address() != null)
                    // Filter by country code if provided and matches
                    .filter(loc -> !StringUtils.hasText(countryCode) 
                            || (loc.address().countryCode() != null && loc.address().countryCode().equalsIgnoreCase(countryCode.trim())))
                    .map(this::toCityResponse)
                    .toList();
        } catch (RuntimeException ex) {
            return Collections.emptyList();
        }
    }

    public GeoPoint findCityCoordinates(String cityName, String countryName) {
         if (!amadeusClientService.isConfigured() || !StringUtils.hasText(cityName)) {
            return null;
        }

        String token = amadeusClientService.fetchAccessToken();
        if (!StringUtils.hasText(token)) {
            return null;
        }

        try {
            // Search for the city
            AmadeusLocationResponse response = amadeusClientService.restClient().get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/reference-data/locations")
                            .queryParam("subType", "CITY")
                            .queryParam("keyword", cityName.trim())
                            .build())
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(AmadeusLocationResponse.class);

            if (response == null || response.data() == null || response.data().isEmpty()) {
                return null;
            }

            // Find the best match (optionally filtering by countryName if possible, but keyword search is usually enough for coordinates)
            // Here we try to match country name if provided to be more precise
            return response.data().stream()
                    .filter(loc -> loc.geoCode() != null)
                    .filter(loc -> !StringUtils.hasText(countryName) 
                            || (loc.address() != null && loc.address().countryName() != null && loc.address().countryName().equalsIgnoreCase(countryName.trim())))
                    .findFirst()
                    .map(loc -> new GeoPoint(loc.geoCode().latitude(), loc.geoCode().longitude()))
                    .orElse(null);
            
        } catch (RuntimeException ex) {
            return null;
        }
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
