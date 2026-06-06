package com.eta.authservice.eventProducer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoEvent {
    private String userId;    // User's user id
    private String username;  // User's username
    private String firstName; // User's first name
    private String lastName;  // User's last name
    private Long phoneNumber; // Contact number
    private String email;     // User email
}
