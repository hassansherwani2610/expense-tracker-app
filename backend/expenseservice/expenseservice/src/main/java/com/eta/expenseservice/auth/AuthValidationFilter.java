package com.eta.expenseservice.auth;

import com.eta.expenseservice.response.TokenValidationResponse;
import com.eta.expenseservice.service.AuthClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class AuthValidationFilter extends OncePerRequestFilter {

    private final AuthClient authClient;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("===== FILTER HIT =====");

        String authHeader =
                request.getHeader("Authorization");

        System.out.println("AUTH HEADER = " + authHeader);

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            System.out.println("NO AUTH HEADER");

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Missing or invalid token"
            );
            return;
        }

        try {

            String token =
                    authHeader.substring(7);

            System.out.println("VALIDATING TOKEN...");

            TokenValidationResponse user =
                    authClient.validate(token);

            System.out.println("TOKEN VALIDATED");

            System.out.println("USER ID = " +
                    user.getUserId());

            System.out.println("USERNAME = " +
                    user.getUsername());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            null,
                            Collections.emptyList()
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            request.setAttribute(
                    "userId",
                    user.getUserId());

            request.setAttribute(
                    "username",
                    user.getUsername());

            filterChain.doFilter(
                    request,
                    response);

        } catch (Exception e) {

            System.out.println("TOKEN VALIDATION FAILED");

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Token validation failed"
            );
        }
    }
}