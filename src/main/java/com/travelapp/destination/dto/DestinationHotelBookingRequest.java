package com.travelapp.destination.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DestinationHotelBookingRequest(
        @NotBlank String offerId,
        @NotBlank @Size(max = 10) String guestTitle,
        @NotBlank @Size(max = 80) String guestFirstName,
        @NotBlank @Size(max = 80) String guestLastName,
        @NotBlank @Size(max = 40) String guestPhone,
        @NotBlank @Email @Size(max = 120) String guestEmail,
        @NotBlank @Size(max = 4) String cardVendorCode,
        @NotBlank @Size(max = 32) String cardNumber,
        @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}", message = "Expiry date must be in YYYY-MM format") String cardExpiryDate,
        @Size(max = 120) String cardHolderName
) {}
