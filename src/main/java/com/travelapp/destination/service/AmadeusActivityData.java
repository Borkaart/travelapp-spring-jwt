package com.travelapp.destination.service;

import java.util.List;

public record AmadeusActivityData(
        String id,
        String name,
        String description,
        String shortDescription,
        Price price,
        List<String> pictures,
        String bookingLink,
        Double rating
) {
    public record Price(
            String amount,
            String currencyCode
    ) {}
}
