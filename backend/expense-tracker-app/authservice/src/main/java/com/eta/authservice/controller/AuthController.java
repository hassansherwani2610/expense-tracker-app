package com.eta.authservice.controller;

import com.eta.authservice.entities.RefreshToken;
import com.eta.authservice.entities.UserInfo;
import com.eta.authservice.model.UserInfoDto;
import com.eta.authservice.repository.UserRepository;
import com.eta.authservice.response.JwtResponseDto;
import com.eta.authservice.service.JwtService;
import com.eta.authservice.service.RefreshTokenService;
import com.eta.authservice.service.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/auth/v1")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private JwtService jwtService;
    private RefreshTokenService refreshTokenService;
    private UserDetailsServiceImpl userDetailsServiceImpl;

    private UserRepository userRepository;

    // Constructor injection
    public AuthController(JwtService jwtService, RefreshTokenService refreshTokenService,
                          UserDetailsServiceImpl userDetailsServiceImpl, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserInfoDto userInfoDto){
        try{
            String userId = userDetailsServiceImpl.signUp(userInfoDto); // Attempt to register new user

            if (userId == null){
                log.warn("Signup failed: User {} already exists", userInfoDto.getUsername());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
            }

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getUsername()); // Generate refresh token
            String jwtToken = jwtService.generateToken(userInfoDto.getUsername(), userId); // Generate access token

            JwtResponseDto jwtResponseDto = JwtResponseDto.builder()
                    .accessToken(jwtToken)
                    .build(); // Build JWT response payload

            log.info("User {} signed up successfully", userInfoDto.getUsername());

            return ResponseEntity.ok(jwtResponseDto); // Return tokens to client
        } catch (Exception exception){
            log.error("Exception during user signup", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error during signup"); // Handle unexpected errors
        }
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()){
            String userId = userDetailsServiceImpl.getUserByUsername(authentication.getName());
            if (Objects.nonNull(userId)){
                return ResponseEntity.ok(userId);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
    }

    @GetMapping("/health")
    public ResponseEntity<Boolean> checkHealth(){
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(
            @RequestHeader(value = "Authorization", required = false)
            String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);

        String username = jwtService.extractUsername(token);

        if (username == null) {
            return ResponseEntity.status(401).build();
        }

        UserDetails userDetails =
                userDetailsServiceImpl.loadUserByUsername(username);

        if (!jwtService.validateToken(token, userDetails)) {
            return ResponseEntity.status(401).build();
        }

        UserInfo user =
                userRepository.findByUsername(username);

        return ResponseEntity.ok(
                Map.of(
                        "userId", user.getUserId(),
                        "username", user.getUsername()
                )
        );
    }
}
