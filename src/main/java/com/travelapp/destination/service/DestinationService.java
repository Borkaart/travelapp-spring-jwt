package com.travelapp.destination.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.travelapp.destination.domain.Destination;
import com.travelapp.destination.dto.DestinationCreateRequest;
import com.travelapp.destination.dto.DestinationResponse;
import com.travelapp.destination.dto.DestinationUpdateRequest;
import com.travelapp.destination.repository.DestinationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DestinationService {

    private final DestinationRepository destinationRepository;

    public DestinationResponse create(DestinationCreateRequest request) {
        Destination destination = Destination.builder()
                .name(request.name())
                .country(request.country())
                .description(request.description())
                .imageUrl(request.imageUrl())
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

    public DestinationResponse getById(Long id) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Destination not found"));
        return toResponse(destination);
    }

    public DestinationResponse update(Long id, DestinationUpdateRequest request) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Destination not found"));

        destination.setName(request.name());
        destination.setCountry(request.country());
        destination.setDescription(request.description());
        destination.setImageUrl(request.imageUrl());

        Destination saved = destinationRepository.save(destination);
        return toResponse(saved);
    }

    public void delete(Long id) {
        if (!destinationRepository.existsById(id)) {
            throw new EntityNotFoundException("Destination not found");
        }
        destinationRepository.deleteById(id);
    }

    private DestinationResponse toResponse(Destination destination) {
        return DestinationResponse.builder()
                .id(destination.getId())
                .name(destination.getName())
                .country(destination.getCountry())
                .description(destination.getDescription())
                .imageUrl(destination.getImageUrl())
                .build();
    }

}
