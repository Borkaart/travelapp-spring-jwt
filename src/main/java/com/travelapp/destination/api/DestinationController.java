package com.travelapp.destination.api;

import com.travelapp.destination.dto.DestinationCreateRequest;
import com.travelapp.destination.dto.DestinationResponse;
import com.travelapp.destination.dto.DestinationUpdateRequest;
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


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public DestinationResponse getById(@PathVariable Long id) {
        return destinationService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DestinationResponse update(
            @PathVariable Long id,
            @RequestBody @Valid DestinationUpdateRequest request
    ) {
        return destinationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        destinationService.delete(id);
    }
}
