package com.travelapp.trip.api;

import com.travelapp.destination.service.AmadeusTripParserService;
import com.travelapp.destination.service.TripParserResponse;
import com.travelapp.trip.dto.ParseConfirmationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripParserController {

    private final AmadeusTripParserService tripParserService;

    @PostMapping("/{tripId}/parse-confirmation")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<TripParserResponse> parse(
            @PathVariable Long tripId,
            @RequestBody @Valid ParseConfirmationRequest request
    ) {
        TripParserResponse response = tripParserService.parseConfirmation(request.base64Content());
        if (response == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }
}
