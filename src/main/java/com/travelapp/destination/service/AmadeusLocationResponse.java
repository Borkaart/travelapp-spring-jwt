package com.travelapp.destination.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AmadeusLocationResponse(
        List<AmadeusLocation> data
) {}
