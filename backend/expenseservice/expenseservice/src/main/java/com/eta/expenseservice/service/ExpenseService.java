package com.eta.expenseservice.service;

import com.eta.expenseservice.entities.Expense;
import com.eta.expenseservice.model.ExpenseDto;
import com.eta.expenseservice.repository.ExpenseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    private final ObjectMapper objectMapper;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, ObjectMapper objectMapper){
        this.expenseRepository = expenseRepository;
        this.objectMapper = objectMapper;
    }


    private void setCurrency(ExpenseDto expenseDto){
        if (Objects.isNull(expenseDto.getCurrency())){
            expenseDto.setCurrency("pkr");
        }
    }

    public List<ExpenseDto> getExpense(String userId) {

        List<Expense> expenseList =
                expenseRepository.findByUserId(userId);

        return expenseList.stream()
                .map(expense -> ExpenseDto.builder()
                        .externalId(expense.getExternalId())
                        .userId(expense.getUserId())
                        .amount(expense.getAmount())
                        .merchant(expense.getMerchant())
                        .currency(expense.getCurrency())
                        .createdAt(expense.getCreatedAt())
                        .updatedAt(expense.getUpdatedAt())
                        .build())
                .toList();
    }

    public ExpenseDto createExpense(ExpenseDto expenseDto) {

        setCurrency(expenseDto);

        Expense expense = Expense.builder()
                .userId(expenseDto.getUserId())
                .amount(expenseDto.getAmount())
                .merchant(expenseDto.getMerchant())
                .currency(expenseDto.getCurrency())
                .build();

        Expense savedExpense =
                expenseRepository.save(expense);

        return ExpenseDto.builder()
                .externalId(savedExpense.getExternalId())
                .userId(savedExpense.getUserId())
                .amount(savedExpense.getAmount())
                .merchant(savedExpense.getMerchant())
                .currency(savedExpense.getCurrency())
                .createdAt(savedExpense.getCreatedAt())
                .updatedAt(savedExpense.getUpdatedAt())
                .build();
    }

    public ExpenseDto updateExpense(ExpenseDto expenseDto){
        setCurrency(expenseDto);

        Optional<Expense> expenseOptional = expenseRepository.findByUserIdAndExternalId(expenseDto.getUserId(), expenseDto.getExternalId());

        if (expenseOptional.isEmpty()){
            return null;
        }

        Expense expense = expenseOptional.get();

        expense.setAmount(expenseDto.getAmount() != null ? expenseDto.getAmount() : expense.getAmount());
        expense.setMerchant(Strings.isNotBlank(expenseDto.getMerchant()) ? expenseDto.getMerchant() : expense.getMerchant());
        expense.setCurrency(Strings.isNotBlank(expenseDto.getCurrency()) ? expenseDto.getCurrency() : expense.getCurrency());

        Expense updatedExpense = expenseRepository.save(expense);

        return ExpenseDto.builder()
                .externalId(updatedExpense.getExternalId())
                .userId(updatedExpense.getUserId())
                .amount(updatedExpense.getAmount())
                .merchant(updatedExpense.getMerchant())
                .currency(updatedExpense.getCurrency())
                .createdAt(updatedExpense.getCreatedAt())
                .updatedAt(updatedExpense.getUpdatedAt())
                .build();
    }

    public Optional<Expense> getExpenseByUserIdAndExternalId(String userId, String externalId){
        return expenseRepository.findByUserIdAndExternalId(userId, externalId);
    }
}
