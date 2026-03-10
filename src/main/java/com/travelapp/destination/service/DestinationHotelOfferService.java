package com.travelapp.destination.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.travelapp.destination.dto.DestinationHotelOfferResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DestinationHotelOfferService {

    private final AmadeusClientService amadeusClientService;

    public List<DestinationHotelOfferResponse> getOffers(
            String hotelId,
            String checkInDate,
            String checkOutDate,
            Integer adults
    ) {
        if (!amadeusClientService.isConfigured()
                || !StringUtils.hasText(hotelId)
                || !StringUtils.hasText(checkInDate)
                || !StringUtils.hasText(checkOutDate)) {
            return Collections.emptyList();
        }

        String token = amadeusClientService.fetchAccessToken();
        if (!StringUtils.hasText(token)) {
            return Collections.emptyList();
        }

        try {
            JsonNode response = amadeusClientService.restClient().get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v3/shopping/hotel-offers")
                            .queryParam("hotelIds", hotelId.trim())
                            .queryParam("checkInDate", checkInDate)
                            .queryParam("checkOutDate", checkOutDate)
                            .queryParam("adults", adults == null || adults < 1 ? 1 : adults)
                            .queryParam("roomQuantity", 1)
                            .queryParam("paymentPolicy", "NONE")
                            .build())
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null || !response.has("data") || !response.get("data").isArray()) {
                return Collections.emptyList();
            }

            List<DestinationHotelOfferResponse> offers = new ArrayList<>();

            for (JsonNode hotelNode : response.get("data")) {
                String resolvedHotelId = textAt(hotelNode, "hotel", "hotelId");
                String hotelName = textAt(hotelNode, "hotel", "name");

                JsonNode offersNode = hotelNode.get("offers");
                if (offersNode == null || !offersNode.isArray()) {
                    continue;
                }

                for (JsonNode offerNode : offersNode) {
                    offers.add(DestinationHotelOfferResponse.builder()
                            .offerId(textAt(offerNode, "id"))
                            .hotelId(resolvedHotelId)
                            .hotelName(hotelName)
                            .roomDescription(textAt(offerNode, "room", "typeEstimated", "category"))
                            .boardType(textAt(offerNode, "boardType"))
                            .checkInDate(textAt(offerNode, "checkInDate"))
                            .checkOutDate(textAt(offerNode, "checkOutDate"))
                            .adults(intAt(offerNode, "guests", "adults"))
                            .currency(textAt(offerNode, "price", "currency"))
                            .totalPrice(textAt(offerNode, "price", "total"))
                            .cancellationDescription(textAt(offerNode, "policies", "cancellations", 0, "description", "text"))
                            .paymentType(textAt(offerNode, "policies", "paymentType"))
                            .build());
                }
            }

            return offers;
        } catch (RuntimeException ex) {
            return Collections.emptyList();
        }
    }

    private String textAt(JsonNode node, Object... path) {
        JsonNode current = node;
        for (Object segment : path) {
            if (current == null || current.isMissingNode() || current.isNull()) {
                return null;
            }
            if (segment instanceof String key) {
                current = current.get(key);
            } else if (segment instanceof Integer index) {
                current = current.isArray() && current.size() > index ? current.get(index) : null;
            }
        }
        return current == null || current.isNull() ? null : current.asText();
    }

    private Integer intAt(JsonNode node, Object... path) {
        String value = textAt(node, path);
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
