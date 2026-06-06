package com.eta.authservice.controller;

import com.eta.authservice.entities.RefreshToken;
import com.eta.authservice.entities.UserInfo;
import com.eta.authservice.request.AuthRequestDto;
import com.eta.authservice.response.JwtResponseDto;
import com.eta.authservice.service.JwtService;
import com.eta.authservice.service.RefreshTokenService;
import com.eta.authservice.service.UserDetailsServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/v1")
public class TokenController {

    private static final Logger log = LoggerFactory.getLogger(TokenController.class);

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody AuthRequestDto authRequestDto, HttpServletResponse httpServletResponse){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                    (
                            authRequestDto.getUsername(),
                            authRequestDto.getPassword()
                    )
            ); // Authenticate user credentials

            if (authentication.isAuthenticated()){
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDto.getUsername()); // Generate refresh token
//                String accessToken = jwtService.generateToken(authRequestDto.getUsername()); // Generate access token
                UserInfo user = userDetailsServiceImpl.getUserEntityByUsername(authRequestDto.getUsername());
                String accessToken = jwtService.generateToken(user.getUsername(), user.getUserId());

                Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken.getToken());
                refreshTokenCookie.setHttpOnly(true);
                refreshTokenCookie.setSecure(true); // set true in production (HTTPS)
                refreshTokenCookie.setAttribute("SameSite", "None");
                refreshTokenCookie.setPath("/auth/v1/refreshToken");
                refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);

                httpServletResponse.addCookie(refreshTokenCookie);

                JwtResponseDto jwtResponseDto = JwtResponseDto.builder()
                        .accessToken(accessToken)
                        .build(); // Build JWT response

                log.info("User '{}' logged in successfully", authRequestDto.getUsername());
                return ResponseEntity.ok(jwtResponseDto);
            }
            else {
                log.warn("Authentication failed for user '{}'", authRequestDto.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        catch (BadCredentialsException badCredentialsException) {
            log.error("Invalid username/password for '{}'", authRequestDto.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid credentials
        }
        catch (Exception exception){
            log.error("Error during authentication", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Unexpected error handling
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<JwtResponseDto> refreshToken(HttpServletRequest httpServletRequest){
        String refreshTokenValue = null;

        if(httpServletRequest.getCookies() != null){
            for (Cookie cookie : httpServletRequest.getCookies()){
                if (cookie.getName().equals("refreshToken")){
                    refreshTokenValue = cookie.getValue();
                }
            }
        }

        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenValue).orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        refreshTokenService.verifyExpiration(refreshToken);

        String newAccessToken = jwtService.generateToken(refreshToken.getUserInfo().getUsername(), refreshToken.getUserInfo().getUserId());

        return ResponseEntity.ok(
                JwtResponseDto.builder()
                        .accessToken(newAccessToken)
                        .build()
        );

//        return refreshTokenService.findByToken(refreshTokenRequestDto.getToken())
//                .map(refreshTokenService::verifyExpiration) // Validate refresh token expiration
//                .map(RefreshToken::getUserInfo) // Extract associated user
//                .map(userInfo -> {
//                    String newAccessToken = jwtService.generateToken(userInfo.getUsername()); // Generate new access token
//
//                    JwtResponseDto jwtResponseDto = JwtResponseDto.builder()
//                            .accessToken(newAccessToken)
//                            .build(); // Reuse existing refresh token
//
//                    log.info("Refresh token used for user '{}'", userInfo.getUsername());
//                    return ResponseEntity.ok(jwtResponseDto);
//                }).orElseThrow(() -> {
//                    log.warn("Refresh token not found or expired: {}", refreshTokenRequestDto.getToken());
//                    return new RuntimeException("Refresh Token is not valid or expired"); // Invalid or expired refresh token
//                });
    }
}
