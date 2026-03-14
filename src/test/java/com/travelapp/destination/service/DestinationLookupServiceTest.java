package com.travelapp.destination.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelapp.destination.dto.DestinationCityResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DestinationLookupServiceTest {

    @Mock
    private AmadeusClientService amadeusClientService;

    @Mock
    private RestClient restClient;
    
    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    
    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;
    
    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private DestinationLookupService destinationLookupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void searchCities_ShouldReturnEmptyList_WhenServiceNotConfigured() {
        when(amadeusClientService.isConfigured()).thenReturn(false);
        List<DestinationCityResponse> result = destinationLookupService.searchCities("US", "New York");
        assertTrue(result.isEmpty());
    }

    @Test
    void searchCities_ShouldReturnEmptyList_WhenQueryIsEmpty() {
        when(amadeusClientService.isConfigured()).thenReturn(true);
        List<DestinationCityResponse> result = destinationLookupService.searchCities("US", "");
        assertTrue(result.isEmpty());
    }
    
    @Test
    void searchCities_ShouldReturnFallback_WhenApisFail() {
        when(amadeusClientService.isConfigured()).thenReturn(false);
        // Photon is called via a static RestClient.builder() in the implementation, 
        // which is hard to mock without PowerMock or similar. 
        // But we can test if the fallback logic is reached when results are empty.
        
        List<DestinationCityResponse> result = destinationLookupService.searchCities("BR", "São Paulo");
        assertFalse(result.isEmpty());
        assertEquals("São Paulo", result.get(0).getName());
        assertEquals("BR", result.get(0).getCountryCode());
    }

    @Test
    void searchCities_ShouldUseCache() {
        when(amadeusClientService.isConfigured()).thenReturn(false);
        
        // First call populates cache (from fallback in this mock environment)
        List<DestinationCityResponse> firstResult = destinationLookupService.searchCities("BR", "Rio");
        assertFalse(firstResult.isEmpty());
        
        // Second call should return same results from cache
        List<DestinationCityResponse> secondResult = destinationLookupService.searchCities("BR", "Rio");
        assertEquals(firstResult, secondResult);
    }
    
    @Test
    void testJsonParsing() throws Exception {
        // This test verifies that the JSON structure matches what we expect
        // using the classes directly, without mocking the full RestClient chain which is complex
        
        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = """
            {
              "data": [
                {
                  "type": "location",
                  "subType": "CITY",
                  "name": "PARIS",
                  "detailedName": "PARIS/FR:CDG",
                  "id": "CPARIS",
                  "self": {
                    "href": "https://test.api.amadeus.com/v1/reference-data/locations/CPARIS",
                    "methods": [
                      "GET"
                    ]
                  },
                  "timeZoneOffset": "+01:00",
                  "iataCode": "PAR",
                  "geoCode": {
                    "latitude": 48.85341,
                    "longitude": 2.3488
                  },
                  "address": {
                    "cityName": "PARIS",
                    "cityCode": "PAR",
                    "countryName": "FRANCE",
                    "countryCode": "FR",
                    "regionCode": "EU"
                  },
                  "analytics": {
                    "travelers": {
                      "score": 100
                    }
                  }
                }
              ]
            }
            """;
            
        AmadeusLocationResponse response = mapper.readValue(jsonResponse, AmadeusLocationResponse.class);
        assertNotNull(response);
        assertNotNull(response.data());
        assertEquals(1, response.data().size());
        
        AmadeusLocation loc = response.data().get(0);
        assertEquals("PARIS", loc.name());
        assertEquals("CITY", loc.subType());
        
        assertNotNull(loc.address());
        assertEquals("FR", loc.address().countryCode());
        
        assertNotNull(loc.geoCode());
        assertEquals(48.85341, loc.geoCode().latitude());
    }
}
