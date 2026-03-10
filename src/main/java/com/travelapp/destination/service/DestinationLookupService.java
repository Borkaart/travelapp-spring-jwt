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
@EnableConfigurationProperties(GeoapifyProperties.class)
public class DestinationLookupService {

    private static final int DEFAULT_LIMIT = 10;
    private static final String REST_COUNTRIES_BASE_URL = "https://restcountries.com";

    private final GeoapifyProperties geoapifyProperties;

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
        if (!geoapifyProperties.enabled() || !StringUtils.hasText(geoapifyProperties.apiKey())) {
            return Collections.emptyList();
        }
        if (!StringUtils.hasText(countryCode) || !StringUtils.hasText(query)) {
            return Collections.emptyList();
        }

        try {
            GeoapifyFeatureCollection response = RestClient.builder()
                    .baseUrl(resolveGeoapifyBaseUrl())
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/geocode/autocomplete")
                            .queryParam("text", query.trim())
                            .queryParam("type", "city")
                            .queryParam("filter", "countrycode:" + countryCode.trim().toLowerCase(Locale.ROOT))
                            .queryParam("limit", DEFAULT_LIMIT)
                            .queryParam("apiKey", geoapifyProperties.apiKey().trim())
                            .build())
                    .retrieve()
                    .body(GeoapifyFeatureCollection.class);

            if (response == null || response.features() == null) {
                return Collections.emptyList();
            }

            return response.features().stream()
                    .map(GeoapifyFeature::properties)
                    .filter(Objects::nonNull)
                    .filter(properties -> StringUtils.hasText(properties.city()) || StringUtils.hasText(properties.name()))
                    .collect(
                            LinkedHashMap<String, DestinationCityResponse>::new,
                            (cities, properties) -> {
                                DestinationCityResponse city = toCityResponse(properties);
                                cities.putIfAbsent(buildCityKey(city), city);
                            },
                            Map::putAll
                    )
                    .values()
                    .stream()
                    .toList();
        } catch (RuntimeException ex) {
            return Collections.emptyList();
        }
    }

    private DestinationCityResponse toCityResponse(GeoapifyFeatureProperties properties) {
        String cityName = StringUtils.hasText(properties.city()) ? properties.city() : properties.name();

        return DestinationCityResponse.builder()
                .name(cityName)
                .country(properties.country())
                .countryCode(properties.countryCode())
                .formatted(properties.formatted())
                .lat(properties.lat())
                .lon(properties.lon())
                .build();
    }

    private String resolveGeoapifyBaseUrl() {
        if (StringUtils.hasText(geoapifyProperties.baseUrl())) {
            return geoapifyProperties.baseUrl().trim();
        }
        return "https://api.geoapify.com";
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
