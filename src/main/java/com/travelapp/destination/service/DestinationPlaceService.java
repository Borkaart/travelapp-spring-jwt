package com.travelapp.destination.service;

import com.travelapp.destination.domain.Destination;
import com.travelapp.destination.dto.DestinationPlaceResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DestinationPlaceService {

    private static final Logger logger = LoggerFactory.getLogger(DestinationPlaceService.class);

    private static final int DEFAULT_RADIUS_KM = 5;
    private static final int DEFAULT_LIMIT = 20;
    private static final String OVERPASS_API_URL = "https://overpass-api.de/api/interpreter";

    private final DestinationLookupService destinationLookupService;
    private final DestinationImageService destinationImageService;

    @Cacheable(value = "destinationPlaces", key = "{#destination.id, #categoryGroup, #sortBy}")
    public List<DestinationPlaceResponse> getPlaces(Destination destination, String categoryGroup, String sortBy) {
        GeoPoint geoPoint = destinationLookupService.findCityCoordinates(destination.getName(), destination.getCountry());
        if (geoPoint == null) {
            logger.warn("Could not find coordinates for destination: {}", destination.getName());
            return Collections.emptyList();
        }

        try {
            logger.info("Fetching points of interest for {} using Overpass API (categoryGroup: {}, sortBy: {})", 
                destination.getName(), categoryGroup, sortBy);
            
            // Optimized Overpass query for tourist attractions, historic sites, museums, and parks
            String query = String.format(
                "[out:json][timeout:25];" +
                "(" +
                  "node[\"tourism\"](around:%d,%f,%f);" +
                  "node[\"historic\"](around:%d,%f,%f);" +
                  "node[\"leisure\"=\"park\"](around:%d,%f,%f);" +
                ");" +
                "out body %d;",
                DEFAULT_RADIUS_KM * 1000, geoPoint.lat(), geoPoint.lon(),
                DEFAULT_RADIUS_KM * 1000, geoPoint.lat(), geoPoint.lon(),
                DEFAULT_RADIUS_KM * 1000, geoPoint.lat(), geoPoint.lon(),
                DEFAULT_LIMIT
            );

            // IMPORTANT: Use URLEncoder to avoid 400 Bad Request error on Overpass API
            String encodedData = java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);

            Map<String, Object> response = RestClient.builder()
                    .baseUrl(OVERPASS_API_URL)
                    .build()
                    .post()
                    .contentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED)
                    .body("data=" + encodedData)
                    .retrieve()
                    .body(Map.class);

            if (response == null || !(response.get("elements") instanceof List<?> elements)) {
                return Collections.emptyList();
            }

            List<DestinationPlaceResponse> places = elements.stream()
                    .filter(e -> e instanceof Map)
                    .map(e -> (Map<String, Object>) e)
                    .map(e -> toPlaceResponse(e, destination.getCountry()))
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            // Apply filters
            if (StringUtils.hasText(categoryGroup)) {
                places = places.stream()
                        .filter(p -> categoryGroup.equalsIgnoreCase(p.getCategoryGroup()))
                        .toList();
            }

            // Apply sorting
            if ("proximity".equalsIgnoreCase(sortBy)) {
                places = places.stream()
                        .sorted(Comparator.comparingDouble(p -> calculateDistance(geoPoint.lat(), geoPoint.lon(), p.getLat(), p.getLon())))
                        .toList();
            } else {
                // Default sorting by "popularity" (simulated by name length or rating)
                places = places.stream()
                        .sorted(Comparator.comparing(DestinationPlaceResponse::getName))
                        .toList();
            }

            logger.info("Found {} points of interest for {}", places.size(), destination.getName());
            return places.stream().limit(DEFAULT_LIMIT).toList();
        } catch (Exception ex) {
            logger.error("Error fetching places from Overpass API", ex);
            return Collections.emptyList();
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        if (lat2 == null || lon2 == null) return Double.MAX_VALUE;
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515 * 1.609344;
        return dist;
    }

    private DestinationPlaceResponse toPlaceResponse(Map<String, Object> element, String country) {
        Map<String, Object> tags = (Map<String, Object>) element.get("tags");
        if (tags == null) return null;

        String name = (String) tags.get("name");
        if (!StringUtils.hasText(name)) return null;

        Double lat = (Double) element.get("lat");
        Double lon = (Double) element.get("lon");

        String tourism = (String) tags.get("tourism");
        String historic = (String) tags.get("historic");
        String leisure = (String) tags.get("leisure");
        String category = tourism != null ? tourism : (historic != null ? "historic" : leisure);
        
        String categoryGroup = resolveCategoryGroup(tags);
        String description = (String) tags.get("description");
        if (!StringUtils.hasText(description)) {
            description = "Ponto de interesse em " + name + ".";
        }

        String website = (String) tags.get("website");
        String openingHours = (String) tags.get("opening_hours");
        String fee = (String) tags.get("fee");
        String price = "yes".equalsIgnoreCase(fee) ? "Consultar no local" : ("no".equalsIgnoreCase(fee) ? "Gratuito" : "Informação não disponível");

        // Use DestinationImageService to get a high-quality image from Unsplash
        String imageUrl = destinationImageService.resolveImageUrl(name, country, null);

        List<String> visitationTips = new ArrayList<>();
        visitationTips.add("Tente chegar cedo para evitar filas.");
        if ("yes".equalsIgnoreCase((String) tags.get("wheelchair"))) {
            visitationTips.add("Acessível para cadeirantes.");
        }

        List<String> suggestedRoutes = new ArrayList<>();
        if ("CULTURAL".equals(categoryGroup)) {
            suggestedRoutes.add("Rota do Centro Histórico");
        } else if ("NATURAL".equals(categoryGroup)) {
            suggestedRoutes.add("Tour dos Parques");
        }

        return DestinationPlaceResponse.builder()
                .name(name)
                .category(category)
                .categoryGroup(categoryGroup)
                .description(description)
                .formatted(name)
                .website(website)
                .imageUrl(imageUrl)
                .lat(lat)
                .lon(lon)
                .openingHours(openingHours != null ? translateOpeningHours(openingHours) : "Consulte o site oficial")
                .price(price)
                .rating(4.5)
                .visitationTips(visitationTips)
                .suggestedRoutes(suggestedRoutes)
                .build();
    }

    private String resolveCategoryGroup(Map<String, Object> tags) {
        if (tags.containsKey("tourism") || tags.containsKey("historic") || tags.containsKey("museum")) {
            return "CULTURAL";
        }
        if (tags.containsKey("leisure") || "park".equals(tags.get("leisure")) || "natural".equals(tags.get("natural"))) {
            return "NATURAL";
        }
        if (tags.containsKey("amenity") && "restaurant".equals(tags.get("amenity"))) {
            return "GASTRONOMICA";
        }
        return "OUTROS";
    }

    private String translateOpeningHours(String hours) {
        // Simple translation for common OSM opening_hours format
        return hours.replace("Mo", "Seg").replace("Tu", "Ter").replace("We", "Qua")
                    .replace("Th", "Qui").replace("Fr", "Sex").replace("Sa", "Sáb")
                    .replace("Su", "Dom").replace("24/7", "Aberto 24h");
    }
}
