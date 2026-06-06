package com.eta.authservice.repository;

import com.eta.authservice.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, String> {
    public UserInfo findByUsername(String username); // Retrieve user by username
}
