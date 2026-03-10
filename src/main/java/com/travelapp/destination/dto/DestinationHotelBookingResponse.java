package com.travelapp.destination.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DestinationHotelBookingResponse {
    private String bookingId;
    private String providerConfirmationId;
    private String hotelName;
    private String status;
    private String checkInDate;
    private String checkOutDate;
    private String currency;
    private String totalPrice;
    private String guestName;
}
