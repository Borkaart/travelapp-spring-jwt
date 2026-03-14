package com.travelapp.destination.api;

import com.travelapp.destination.dto.DestinationCreateRequest;
import com.travelapp.destination.dto.DestinationCityResponse;
import com.travelapp.destination.dto.DestinationCountryResponse;
import com.travelapp.destination.dto.DestinationHotelBookingRequest;
import com.travelapp.destination.dto.DestinationHotelBookingResponse;
import com.travelapp.destination.dto.DestinationHotelResponse;
import com.travelapp.destination.dto.DestinationHotelOfferResponse;
import com.travelapp.destination.dto.DestinationPlaceResponse;
import com.travelapp.destination.dto.DestinationResponse;
import com.travelapp.destination.dto.DestinationResolveRequest;
import com.travelapp.destination.dto.DestinationUpdateRequest;
import com.travelapp.destination.service.AmadeusActivityData;
import com.travelapp.destination.service.AmadeusActivityService;
import com.travelapp.destination.service.DestinationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinations")
@RequiredArgsConstructor
public class DestinationController {

    private final DestinationService destinationService;
    private final AmadeusActivityService amadeusActivityService; // Added field

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public DestinationResponse create(@RequestBody @Valid DestinationCreateRequest request) {
        return destinationService.create(request);
    }

    @GetMapping
    public Page<DestinationResponse> list(Pageable pageable) {
        return destinationService.findAll(pageable);
    }

    @GetMapping("/countries")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<DestinationCountryResponse> searchCountries(@RequestParam(required = false) String q) {
        return destinationService.searchCountries(q);
    }

    @GetMapping("/cities")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<DestinationCityResponse> searchCities(
            @RequestParam String countryCode,
            @RequestParam String q
    ) {
        return destinationService.searchCities(countryCode, q);
    }

    @PostMapping("/resolve")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public DestinationResponse resolve(@RequestBody @Valid DestinationResolveRequest request) {
        return destinationService.resolve(request);
    }

    @PostMapping("/refresh-missing-images")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DestinationResponse> refreshMissingImages() {
        return destinationService.refreshMissingImages();
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public DestinationResponse getById(@PathVariable Long id) {
        return destinationService.getById(id);
    }

    @GetMapping("/{id}/places")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<DestinationPlaceResponse> getPlaces(
            @PathVariable Long id,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "popularity") String sortBy
    ) {
        return destinationService.getPlaces(id, category, sortBy);
    }

    @GetMapping("/{id}/hotels")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<DestinationHotelResponse> getHotels(@PathVariable Long id) {
        return destinationService.getHotels(id);
    }

    @GetMapping("/{id}/activities")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<AmadeusActivityData> getActivities(@PathVariable Long id) {
        return destinationService.getActivities(id);
    }

    @GetMapping("/hotels/{hotelId}/offers")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<DestinationHotelOfferResponse> getHotelOffers(
            @PathVariable String hotelId,
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate,
            @RequestParam(defaultValue = "1") Integer adults
    ) {
        return destinationService.getHotelOffers(hotelId, checkInDate, checkOutDate, adults);
    }

    @PostMapping("/hotels/bookings")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public DestinationHotelBookingResponse bookHotelOffer(@RequestBody @Valid DestinationHotelBookingRequest request) {
        return destinationService.bookHotelOffer(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DestinationResponse update(
            @PathVariable Long id,
            @RequestBody @Valid DestinationUpdateRequest request
    ) {
        return destinationService.update(id, request);
    }

    @PostMapping("/{id}/refresh-image")
    @PreAuthorize("hasRole('ADMIN')")
    public DestinationResponse refreshImage(@PathVariable Long id) {
        return destinationService.refreshImage(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        destinationService.delete(id);
    }

    @GetMapping("/search-activities")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<AmadeusActivityData> searchActivities(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(required = false) Integer radius) {
        return amadeusActivityService.searchActivities(lat, lon, radius);
    }
}
