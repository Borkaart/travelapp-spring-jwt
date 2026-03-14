package com.travelapp.destination.service;

public record TripParserRequest(
    Data data
) {
    public record Data(
        String content
    ) {}
}
