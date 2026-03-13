package com.travelapp.destination.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class DestinationPlaceResponse {
    private String name;
    private String category;
    private String categoryGroup; // CULTURAL, NATURAL, GASTRONOMICA, etc.
    private String description;
    private String formatted;
    private String website;
    private String imageUrl;
    private Double lat;
    private Double lon;
    private String openingHours;
    private String price; // Ex: "Gratuito", "€15.00"
    private Double rating;
    private List<String> visitationTips;
    private List<String> suggestedRoutes; // Ex: ["Rota Histórica", "Tour Gastronômico"]
    private String shortVideoUrl;
}
