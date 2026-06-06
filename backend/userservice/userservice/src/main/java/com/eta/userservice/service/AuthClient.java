package com.eta.userservice.service;

import com.eta.userservice.response.TokenValidationResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthClient {

    private final RestTemplate restTemplate;

    public AuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TokenValidationResponse validate(String token) {

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(token);

        HttpEntity<Void> entity =
                new HttpEntity<>(headers);

        ResponseEntity<TokenValidationResponse> response =
                restTemplate.exchange(
                        "http://authservice:9898/auth/v1/validate",
                        HttpMethod.GET,
                        entity,
                        TokenValidationResponse.class
                );

        return response.getBody();
    }
}