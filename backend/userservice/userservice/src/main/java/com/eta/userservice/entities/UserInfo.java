package com.eta.userservice.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // Ensure JSON fields follow snake_case format
//@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "user_info")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true)
    private String userId;

    @Column(name = "username", length = 30, nullable = false, unique = true)
    private String username;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number", unique = true)
    private Long phoneNumber;

    @Column(nullable = false, unique = true)
    private String email;
}
