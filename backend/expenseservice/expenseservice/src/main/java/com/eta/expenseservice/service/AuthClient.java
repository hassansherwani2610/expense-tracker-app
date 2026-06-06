package com.eta.expenseservice.service;

import com.eta.expenseservice.response.TokenValidationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthClient {

    private final RestTemplate restTemplate;

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