package com.eta.expenseservice.eventConsumer;


import com.eta.expenseservice.model.ExpenseDto;
import com.eta.expenseservice.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ExpenseConsumer {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseConsumer(ExpenseService expenseService){
        this.expenseService = expenseService;
    }

    @KafkaListener(topics = "${spring.kafka.topic-json.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenToKafka(ExpenseDto expenseDtoEvent){
        try{
//            // 1. Basic validation w.r.t: userId OR externalId
//            if (expenseDtoEvent.getUserId() == null || expenseDtoEvent.getExternalId() == null){
//                System.out.println("Invalid event received. Skipping...");
//                return;
//            }
//
//            // 2. Check duplicate (Idempotency) w.r.t: userId AND externalId
//            boolean expenseExists = expenseService.getExpenseByUserIdAndExternalId(expenseDtoEvent.getUserId(), expenseDtoEvent.getExternalId()).isPresent();
//            if (expenseExists){
//                System.out.println("Duplicate event detected. Skipping...");
//                return;
//            }

            // 3. Save expense
            expenseService.createExpense(expenseDtoEvent);
        }catch (Exception exception){
            exception.printStackTrace();
            System.out.println("ExpenseServiceConsumer: Exception is thrown while consuming kafka event.");
        }
    }
}
