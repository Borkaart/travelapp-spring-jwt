package com.travelapp.destination.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.travelapp.destination.domain.Destination;
import com.travelapp.destination.dto.DestinationCityResponse;
import com.travelapp.destination.dto.DestinationCreateRequest;
import com.travelapp.destination.dto.DestinationCountryResponse;
import com.travelapp.destination.dto.DestinationHotelBookingRequest;
import com.travelapp.destination.dto.DestinationHotelBookingResponse;
import com.travelapp.destination.dto.DestinationHotelResponse;
import com.travelapp.destination.dto.DestinationHotelOfferResponse;
import com.travelapp.destination.dto.DestinationPlaceResponse;
import com.travelapp.destination.dto.DestinationResponse;
import com.travelapp.destination.dto.DestinationResolveRequest;
import com.travelapp.destination.dto.DestinationUpdateRequest;
import com.travelapp.destination.repository.DestinationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DestinationService {

    private final DestinationRepository destinationRepository;
    private final DestinationImageService destinationImageService;
    private final DestinationPlaceService destinationPlaceService;
    private final DestinationHotelService destinationHotelService;
    private final DestinationHotelOfferService destinationHotelOfferService;
    private final DestinationHotelBookingService destinationHotelBookingService;
    private final DestinationLookupService destinationLookupService;

    public DestinationResponse create(DestinationCreateRequest request) {
        Destination destination = Destination.builder()
                .name(request.name())
                .country(request.country())
                .description(request.description())
                .imageUrl(destinationImageService.resolveImageUrl(request.name(), request.country(), request.imageUrl()))
                .createdAt(LocalDateTime.now())
                .build();

        Destination saved = destinationRepository.save(destination);
        return toResponse(saved);
    }

    public Page<DestinationResponse> findAll(Pageable pageable) {
        return destinationRepository.findAll(pageable)
                .map(this::toResponse);
    }

    public List<DestinationResponse> list() {
        return destinationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DestinationResponse> refreshMissingImages() {
        return destinationRepository.findAll()
                .stream()
                .filter(destination -> destination.getImageUrl() == null || destination.getImageUrl().isBlank())
                .map(destination -> {
                    destination.setImageUrl(destinationImageService.resolveImageUrl(
                            destination.getName(),
                            destination.getCountry(),
                            null
                    ));
                    return destinationRepository.save(destination);
                })
                .map(this::toResponse)
                .toList();
    }

    public DestinationResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public DestinationResponse update(Long id, DestinationUpdateRequest request) {
        Destination destination = findById(id);

        destination.setName(request.name());
        destination.setCountry(request.country());
        destination.setDescription(request.description());
        destination.setImageUrl(destinationImageService.resolveImageUrl(
                request.name(),
                request.country(),
                request.imageUrl()
        ));

        Destination saved = destinationRepository.save(destination);
        return toResponse(saved);
    }

    public DestinationResponse refreshImage(Long id) {
        Destination destination = findById(id);
        destination.setImageUrl(destinationImageService.resolveImageUrl(
                destination.getName(),
                destination.getCountry(),
                null
        ));
        return toResponse(destinationRepository.save(destination));
    }

    public List<DestinationPlaceResponse> getPlaces(Long id, String categoryGroup, String sortBy) {
        Destination destination = findById(id);
        return destinationPlaceService.getPlaces(destination, categoryGroup, sortBy);
    }

    public List<DestinationHotelResponse> getHotels(Long id) {
        Destination destination = findById(id);
        return destinationHotelService.getHotels(destination);
    }

    public List<DestinationHotelOfferResponse> getHotelOffers(
            String hotelId,
            String checkInDate,
            String checkOutDate,
            Integer adults
    ) {
        return destinationHotelOfferService.getOffers(hotelId, checkInDate, checkOutDate, adults);
    }

    public DestinationHotelBookingResponse bookHotelOffer(DestinationHotelBookingRequest request) {
        return destinationHotelBookingService.book(request);
    }

    public List<DestinationCountryResponse> searchCountries(String query) {
        return destinationLookupService.searchCountries(query);
    }

    public List<DestinationCityResponse> searchCities(String countryCode, String query) {
        return destinationLookupService.searchCities(countryCode, query);
    }

    public DestinationResponse resolve(DestinationResolveRequest request) {
        String normalizedName = request.name().trim();
        String normalizedCountry = request.country().trim();

        Destination destination = destinationRepository.findByNameIgnoreCaseAndCountryIgnoreCase(
                        normalizedName,
                        normalizedCountry
                )
                .orElseGet(() -> destinationRepository.save(Destination.builder()
                        .name(normalizedName)
                        .country(normalizedCountry)
                        .description(buildAutoDescription(normalizedName, normalizedCountry))
                        .imageUrl(destinationImageService.resolveImageUrl(normalizedName, normalizedCountry, null))
                        .createdAt(LocalDateTime.now())
                        .build()));

        if (!StringUtils.hasText(destination.getImageUrl())) {
            destination.setImageUrl(destinationImageService.resolveImageUrl(
                    destination.getName(),
                    destination.getCountry(),
                    null
            ));
            destination = destinationRepository.save(destination);
        }

        return toResponse(destination);
    }

    public void delete(Long id) {
        destinationRepository.delete(findById(id));
    }

    private Destination findById(Long id) {
        return destinationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Destination not found"));
    }

    private DestinationResponse toResponse(Destination destination) {
        return DestinationResponse.builder()
                .id(destination.getId())
                .name(destination.getName())
                .country(destination.getCountry())
                .description(destination.getDescription())
                .imageUrl(destination.getImageUrl())
                .createdAt(destination.getCreatedAt())
                .build();
    }

    private String buildAutoDescription(String name, String country) {
        return "Destino gerado automaticamente para " + name + ", " + country + ".";
    }

}
