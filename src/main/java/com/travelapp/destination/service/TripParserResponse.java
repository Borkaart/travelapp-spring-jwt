package com.travelapp.destination.service;

import java.util.List;

public record TripParserResponse(
    Data data
) {
    public record Data(
        Trip trip
    ) {}

    public record Trip(
        String title,
        String startDate,
        String endDate,
        List<TravelProduct> travelProducts
    ) {}

    public record TravelProduct(
        String startStatus,
        FlightData flightData,
        HotelData hotelData,
        TrainData trainData
    ) {}

    public record FlightData(
        String departureAt,
        String arrivalAt,
        String iataCode,
        String flightNumber,
        Location departureLocation,
        Location arrivalLocation
    ) {}

    public record HotelData(
        String checkInAt,
        String checkOutAt,
        String hotelName,
        Location hotelLocation,
        String confirmationNumber
    ) {}

    public record TrainData(
        String departureAt,
        String arrivalAt,
        Location departureLocation,
        Location arrivalLocation
    ) {}

    public record Location(
        String name,
        String address,
        String cityName,
        String countryCode
    ) {}
}
