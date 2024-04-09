package ca.powercool.powercoolhub.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.http.HttpMethod.GET;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GeocodingServiceTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private GeocodingService geocodingService;

    private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    @Before
    public void setUp() {
        // Prepare your mocks (not actually calling HTTP)
    }

    @Test
    public void testGeocodeAddressSuccessful() {
        String address = "1600 Amphitheatre Parkway, Mountain View, CA";
        URI uri = UriComponentsBuilder.fromHttpUrl(GEOCODE_URL)
            .queryParam("address", address)
            .queryParam("key", "fake-api-key")
            .build()
            .toUri();

        Map<String, Object> responseMap = Map.of(
            "results", List.of(
                Map.of("geometry", Map.of("location", Map.of("lat", 37.4219999, "lng", -122.0840575)))
            ),
            "status", "OK"
        );

        when(restTemplate.getForEntity(uri, Map.class))
            .thenReturn(new ResponseEntity<>(responseMap, HttpStatus.OK));

        Map<String, Double> result = geocodingService.geocodeAddress(address);

        assertNotNull(result);
        assertEquals(Double.valueOf(37.42252380000001), result.get("lat"));
        assertEquals(Double.valueOf(-122.0843049), result.get("lng"));
    }

    @Test
    public void testGeocodeAddressFailure() {
        // Arrange
        String address = "Invalid Address";
        URI uri = UriComponentsBuilder.fromHttpUrl(GeocodingService.GEOCODE_URL)
            .queryParam("address", address)
            .queryParam("key", "fake-api-key")  // Substitute the real key with a fake one for testing.
            .build()
            .toUri();
    
        // Mock the RestTemplate to return a BAD_REQUEST status, simulating a failed geocoding request
        when(restTemplate.getForEntity(uri, Map.class))
            .thenReturn(new ResponseEntity<>(Map.of("status", "ZERO_RESULTS"), HttpStatus.BAD_REQUEST));
    
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> geocodingService.geocodeAddress(address));
        String expectedMessage = "Geocoding failed: ZERO_RESULTS";
        assertTrue("Expected exception message to contain: " + expectedMessage, exception.getMessage().contains(expectedMessage));
    }
}