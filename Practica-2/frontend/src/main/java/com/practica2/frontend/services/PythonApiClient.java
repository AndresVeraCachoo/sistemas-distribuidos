package com.practica2.frontend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class PythonApiClient {

    private final RestTemplate restTemplate;

    @Value("${api.python.url}")
    private String pythonApiUrl;

    public PythonApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> getPokemon(String name) {
        String url = pythonApiUrl + "/api/pokemon/" + name;
        try {
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            return Map.of("error", "No se pudo conectar con el backend de Python");
        }
    }
}