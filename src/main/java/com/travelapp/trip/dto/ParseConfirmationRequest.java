package com.travelapp.trip.dto;

import jakarta.validation.constraints.NotBlank;

public record ParseConfirmationRequest(
    @NotBlank(message = "Conteudo base64 e obrigatorio")
    String base64Content
) {}
