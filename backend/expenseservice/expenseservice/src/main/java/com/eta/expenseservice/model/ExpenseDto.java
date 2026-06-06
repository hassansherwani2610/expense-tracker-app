package com.eta.expenseservice.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpenseDto {
    private String externalId;

    @JsonProperty(value = "user_id")
    @NonNull
    private String userId;

    @JsonProperty(value = "amount")
    private BigDecimal amount;

    @JsonProperty(value = "merchant")
    private String merchant;

    @JsonProperty(value = "currency")
    private String currency;

    @JsonProperty(value = "created_at")
    private Timestamp createdAt;

    private Timestamp updatedAt;

    public ExpenseDto(String jsonInString){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            ExpenseDto expenseDto = objectMapper.readValue(jsonInString, ExpenseDto.class);
            this.externalId = expenseDto.externalId;
            this.userId = expenseDto.userId;
            this.amount = expenseDto.amount;
            this.merchant = expenseDto.merchant;
            this.currency = expenseDto.currency;
            this.createdAt = expenseDto.createdAt;
            this.updatedAt = expenseDto.updatedAt;
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
