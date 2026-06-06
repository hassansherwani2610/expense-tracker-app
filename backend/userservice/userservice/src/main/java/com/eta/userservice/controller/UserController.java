package com.eta.userservice.controller;

import com.eta.userservice.model.UserInfoDto;
import com.eta.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/createUpdateUser")
    public ResponseEntity<UserInfoDto> createUpdateUser(
            @RequestBody UserInfoDto userInfoDto,
            HttpServletRequest request) {

        String userId =
                (String) request.getAttribute("userId");

        String username =
                (String) request.getAttribute("username");

        System.out.println("REQUEST USER ID = " + userId);
        System.out.println("REQUEST USERNAME = " + username);

        userInfoDto.setUserId(userId);
        userInfoDto.setUsername(username);

        UserInfoDto user =
                userService.createOrUpdateUser(userInfoDto);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/getUser")
    public ResponseEntity<UserInfoDto> getUser(
            HttpServletRequest request) throws Exception {

        String userId =
                (String) request.getAttribute("userId");

        return ResponseEntity.ok(
                userService.getUser(userId)
        );
    }
}