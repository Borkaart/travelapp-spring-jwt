package com.travelapp.destination.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelapp.destination.dto.DestinationHotelBookingRequest;
import com.travelapp.destination.dto.DestinationHotelBookingResponse;
import com.travelapp.exception.ApiIntegrationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DestinationHotelBookingService {

    private final AmadeusClientService amadeusClientService;
    private final ObjectMapper objectMapper;

    public DestinationHotelBookingResponse book(DestinationHotelBookingRequest request) {
        if (!amadeusClientService.isConfigured()) {
            throw new ApiIntegrationException("Amadeus hotel booking is not configured.");
        }

        String token = amadeusClientService.fetchAccessToken();
        if (!StringUtils.hasText(token)) {
            throw new ApiIntegrationException("Unable to authenticate with Amadeus.");
        }

        String holderName = StringUtils.hasText(request.cardHolderName())
                ? request.cardHolderName().trim()
                : (request.guestFirstName().trim() + " " + request.guestLastName().trim());

        Map<String, Object> payload = Map.of(
                "data", Map.of(
                        "offerId", request.offerId().trim(),
                        "guests", List.of(Map.of(
                                "id", 1,
                                "name", Map.of(
                                        "title", request.guestTitle().trim().toUpperCase(),
                                        "firstName", request.guestFirstName().trim(),
                                        "lastName", request.guestLastName().trim()
                                ),
                                "contact", Map.of(
                                        "phone", request.guestPhone().trim(),
                                        "email", request.guestEmail().trim()
                                )
                        )),
                        "payments", List.of(Map.of(
                                "id", 1,
                                "method", "creditCard",
                                "card", Map.of(
                                        "vendorCode", request.cardVendorCode().trim().toUpperCase(),
                                        "cardNumber", request.cardNumber().trim(),
                                        "expiryDate", request.cardExpiryDate().trim(),
                                        "holderName", holderName
                                )
                        ))
                )
        );

        try {
            JsonNode response = amadeusClientService.restClient().post()
                    .uri("/v2/booking/hotel-orders")
                    .header("Authorization", "Bearer " + token)
                    .body(payload)
                    .retrieve()
                    .body(JsonNode.class);

            return toResponse(response);
        } catch (RestClientResponseException ex) {
            throw new ApiIntegrationException(extractAmadeusMessage(ex));
        } catch (RuntimeException ex) {
            throw new ApiIntegrationException("Unable to complete hotel booking.");
        }
    }

    private DestinationHotelBookingResponse toResponse(JsonNode response) {
        JsonNode data = response != null ? response.get("data") : null;
        JsonNode hotelOffer = data != null ? data.get("hotelOffer") : null;
        JsonNode guests = data != null ? data.get("guests") : null;
        JsonNode firstGuest = guests != null && guests.isArray() && !guests.isEmpty() ? guests.get(0) : null;

        return DestinationHotelBookingResponse.builder()
                .bookingId(textAt(data, "id"))
                .providerConfirmationId(textAt(data, "associatedRecords", 0, "reference"))
                .hotelName(textAt(hotelOffer, "hotel", "name"))
                .status(textAt(data, "type"))
                .checkInDate(textAt(hotelOffer, "checkInDate"))
                .checkOutDate(textAt(hotelOffer, "checkOutDate"))
                .currency(textAt(hotelOffer, "price", "currency"))
                .totalPrice(textAt(hotelOffer, "price", "total"))
                .guestName(buildGuestName(firstGuest))
                .build();
    }

    private String buildGuestName(JsonNode guestNode) {
        String firstName = textAt(guestNode, "name", "firstName");
        String lastName = textAt(guestNode, "name", "lastName");
        if (!StringUtils.hasText(firstName) && !StringUtils.hasText(lastName)) {
            return null;
        }
        return (firstName == null ? "" : firstName) + (lastName == null ? "" : " " + lastName);
    }

    private String extractAmadeusMessage(RestClientResponseException ex) {
        try {
            JsonNode body = objectMapper.readTree(ex.getResponseBodyAsString());
            JsonNode errors = body.get("errors");
            if (errors != null && errors.isArray() && !errors.isEmpty()) {
                String detail = textAt(errors.get(0), "detail");
                String title = textAt(errors.get(0), "title");
                if (StringUtils.hasText(detail)) {
                    return detail;
                }
                if (StringUtils.hasText(title)) {
                    return title;
                }
            }
        } catch (Exception ignored) {
        }
        return "Unable to complete hotel booking.";
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
}
