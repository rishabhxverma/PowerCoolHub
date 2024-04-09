package ca.powercool.powercoolhub.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.api.key}") // Injected from application.properties or environment variable
    private String apiKey;

    private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    public Map<String, Double> geocodeAddress(String address) {
        URI uri = UriComponentsBuilder.fromHttpUrl(GEOCODE_URL)
                .queryParam("address", address)
                .queryParam("key", apiKey)
                .build()
                .toUri();

        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(uri, Map.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Geocoding API request failed: " + responseEntity.getStatusCode());
        }

        Map<String, Object> responseBody = responseEntity.getBody();
        return parseLocation(responseBody);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Double> parseLocation(Map<String, Object> response) {
        if (response != null && "OK".equals(response.get("status"))) {
            Map<String, Object> result = ((java.util.List<Map<String, Object>>) response.get("results")).get(0);
            Map<String, Object> geometry = (Map<String, Object>) result.get("geometry");
            return (Map<String, Double>) geometry.get("location");
        } else {
            throw new RuntimeException("Geocoding failed: " + (response != null ? response.get("status") : "Unknown error"));
        }
    }
}