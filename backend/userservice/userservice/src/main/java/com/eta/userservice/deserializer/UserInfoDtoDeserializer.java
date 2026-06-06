package com.eta.userservice.deserializer;

import com.eta.userservice.model.UserInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class UserInfoDtoDeserializer implements Deserializer<UserInfoDto> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public UserInfoDto deserialize(String topic, byte[] data) {

        if (data == null) {
            return null;
        }

        try {
            return objectMapper.readValue(data, UserInfoDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {
    }
}