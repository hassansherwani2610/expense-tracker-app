package com.eta.userservice.service;

import com.eta.userservice.entities.UserInfo;
import com.eta.userservice.model.UserInfoDto;
import com.eta.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public UserInfoDto createOrUpdateUser(UserInfoDto userInfoDto){
        Supplier<UserInfo> createUser = () -> {
            return userRepository.save(userInfoDto.transformToUserInfo());
        };

        UnaryOperator<UserInfo> updateUser = user -> {
            user.setUserId(userInfoDto.getUserId());
            user.setUsername(userInfoDto.getUsername());
            user.setFirstName(userInfoDto.getFirstName());
            user.setLastName(userInfoDto.getLastName());
            user.setPhoneNumber(userInfoDto.getPhoneNumber());
            user.setEmail(userInfoDto.getEmail());

            return userRepository.save(user);
        };

        UserInfo userInfo = userRepository.findByUserId(userInfoDto.getUserId())
                .map(updateUser)
                .orElseGet(createUser);

        return new UserInfoDto(
                userInfo.getUserId(),
                userInfo.getUsername(),
                userInfo.getFirstName(),
                userInfo.getLastName(),
                userInfo.getPhoneNumber(),
                userInfo.getEmail()
        );
    }

    public UserInfoDto getUser(String userId) throws Exception {

        Optional<UserInfo> userInfoOptional =
                userRepository.findByUserId(userId);

        if (userInfoOptional.isEmpty()) {
            throw new Exception("User not found");
        }

        UserInfo userInfo = userInfoOptional.get();

        return new UserInfoDto(
                userInfo.getUserId(),
                userInfo.getUsername(),
                userInfo.getFirstName(),
                userInfo.getLastName(),
                userInfo.getPhoneNumber(),
                userInfo.getEmail()
        );
    }
}
