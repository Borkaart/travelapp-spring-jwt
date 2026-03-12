package com.travelapp.destination.service;

import com.travelapp.destination.domain.Destination;
import com.travelapp.destination.dto.DestinationHotelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(AmadeusProperties.class)
public class DestinationHotelService {

    private static final int DEFAULT_RADIUS_KM = 8;

    private final AmadeusClientService amadeusClientService;
    private final DestinationLookupService destinationLookupService;

    public List<DestinationHotelResponse> getHotels(Destination destination) {
        if (!amadeusClientService.isConfigured()) {
            return Collections.emptyList();
        }

        GeoPoint geoPoint = destinationLookupService.findCityCoordinates(destination.getName(), destination.getCountry());
        if (geoPoint == null) {
            return Collections.emptyList();
        }

        String token = amadeusClientService.fetchAccessToken();
        if (!StringUtils.hasText(token)) {
            return Collections.emptyList();
        }

        try {
            AmadeusHotelListResponse response = amadeusClientService.restClient().get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/reference-data/locations/hotels/by-geocode")
                            .queryParam("latitude", geoPoint.lat())
                            .queryParam("longitude", geoPoint.lon())
                            .queryParam("radius", DEFAULT_RADIUS_KM)
                            .queryParam("radiusUnit", "KM")
                            .queryParam("hotelSource", "ALL")
                            .build())
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(AmadeusHotelListResponse.class);

            if (response == null || response.data() == null) {
                return Collections.emptyList();
            }

            return response.data().stream()
                    .filter(Objects::nonNull)
                    .map(this::toHotelResponse)
                    .toList();
        } catch (RuntimeException ex) {
            return Collections.emptyList();
        }
    }

    private DestinationHotelResponse toHotelResponse(AmadeusHotelData hotel) {
        return DestinationHotelResponse.builder()
                .hotelId(hotel.hotelId())
                .name(hotel.name())
                .address(joinAddress(hotel.address()))
                .city(hotel.address() != null ? hotel.address().cityName() : null)
                .countryCode(hotel.address() != null ? hotel.address().countryCode() : null)
                .latitude(hotel.geoCode() != null ? hotel.geoCode().latitude() : null)
                .longitude(hotel.geoCode() != null ? hotel.geoCode().longitude() : null)
                .distanceValue(hotel.distance() != null ? hotel.distance().value() : null)
                .distanceUnit(hotel.distance() != null ? hotel.distance().unit() : null)
                .rating(hotel.rating())
                .build();
    }

    private String joinAddress(AmadeusHotelData.AmadeusAddress address) {
        if (address == null || address.lines() == null || address.lines().isEmpty()) {
            return null;
        }
        return String.join(", ", address.lines());
    }
}
