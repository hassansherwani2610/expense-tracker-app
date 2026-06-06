package com.eta.authservice.model;

import com.eta.authservice.entities.UserInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.SuperBuilder;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // Use snake_case in JSON responses
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@Builder
@SuperBuilder
public class UserInfoDto extends UserInfo {

    @NonNull
    private String username; // User's username

    @NonNull
    private String firstName; // User's first name

    @NonNull
    private String lastName;  // User's last name

    @NonNull
    private Long phoneNumber; // Contact number

    @NonNull
    private String email;     // User email
}
