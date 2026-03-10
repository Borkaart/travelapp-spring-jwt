package com.travelapp.destination.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DestinationHotelOfferResponse {
    private String offerId;
    private String hotelId;
    private String hotelName;
    private String roomDescription;
    private String boardType;
    private String checkInDate;
    private String checkOutDate;
    private Integer adults;
    private String currency;
    private String totalPrice;
    private String cancellationDescription;
    private String paymentType;
}
