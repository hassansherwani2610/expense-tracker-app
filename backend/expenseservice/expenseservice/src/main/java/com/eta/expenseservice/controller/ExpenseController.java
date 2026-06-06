package com.eta.expenseservice.controller;

import com.eta.expenseservice.model.ExpenseDto;
import com.eta.expenseservice.service.ExpenseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense/v1")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/getExpense")
    public ResponseEntity<List<ExpenseDto>> getExpense(
            HttpServletRequest request) {

        try {

            String userId =
                    (String) request.getAttribute("userId");

            List<ExpenseDto> expenseDtoList =
                    expenseService.getExpense(userId);

            return new ResponseEntity<>(
                    expenseDtoList,
                    HttpStatus.OK
            );

        } catch (Exception exception) {

            exception.printStackTrace();

            return new ResponseEntity<>(
                    null,
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @PostMapping("/addExpense")
    public ResponseEntity<ExpenseDto> addExpense(
            HttpServletRequest request,
            @RequestBody ExpenseDto expenseDto) {

        try {

            String userId =
                    (String) request.getAttribute("userId");

            expenseDto.setUserId(userId);

            ExpenseDto savedExpense =
                    expenseService.createExpense(expenseDto);

            return ResponseEntity.ok(savedExpense);

        } catch (Exception exception) {

            exception.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @PutMapping("/updateExpense")
    public ResponseEntity<ExpenseDto> updateExpense(
            HttpServletRequest request,
            @RequestBody ExpenseDto expenseDto) {

        try {

            String userId =
                    (String) request.getAttribute("userId");

            expenseDto.setUserId(userId);

            ExpenseDto updateExpense =
                    expenseService.updateExpense(expenseDto);

            return new ResponseEntity<>(
                    updateExpense,
                    HttpStatus.OK
            );

        } catch (Exception exception) {

            exception.printStackTrace();

            return new ResponseEntity<>(
                    null,
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}