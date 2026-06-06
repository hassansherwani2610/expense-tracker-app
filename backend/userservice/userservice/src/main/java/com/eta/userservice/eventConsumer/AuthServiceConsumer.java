package com.eta.userservice.eventConsumer;


import com.eta.userservice.model.UserInfoDto;
import com.eta.userservice.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceConsumer {

    private final UserService userService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");

    @KafkaListener(topics = "${spring.kafka.topic-json.name}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void listen(UserInfoDto userInfoDtoEvent) {

        System.out.println("KAFKA EVENT RECEIVED");
        System.out.println(userInfoDtoEvent);

        try {
            // Null check
            if (userInfoDtoEvent == null) {
                log.warn("Received null eventData from Kafka");
                return;
            }

            // Validate userId
            if (userInfoDtoEvent.getUserId() == null || userInfoDtoEvent.getUserId().isEmpty()) {
                log.warn("Invalid userId received");
                return;
            }

            // Validate email
            if (userInfoDtoEvent.getEmail() == null || !EMAIL_PATTERN.matcher(userInfoDtoEvent.getEmail()).matches()) {
                log.warn("Invalid email: {}", userInfoDtoEvent.getEmail());
                return;
            }

            // Validate phone number
            if (userInfoDtoEvent.getPhoneNumber() != null && !PHONE_PATTERN.matcher(String.valueOf(userInfoDtoEvent.getPhoneNumber())).matches()) {
                log.warn("Invalid phone number: {}", userInfoDtoEvent.getPhoneNumber());
                return;
            }

            // Use existing service method
            userService.createOrUpdateUser(userInfoDtoEvent);

            log.info("User processed successfully: {}", userInfoDtoEvent.getEmail());

        } catch (Exception exception) {
            log.error("Error consuming Kafka event: {}", userInfoDtoEvent, exception);
            // Rethrow for Kafka retry
            throw exception;
        }
    }
}