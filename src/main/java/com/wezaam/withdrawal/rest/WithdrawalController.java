package com.wezaam.withdrawal.rest;

import com.wezaam.withdrawal.model.PaymentMethod;
import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.repository.PaymentMethodRepository;
import com.wezaam.withdrawal.repository.WithdrawalRepository;
import com.wezaam.withdrawal.repository.UserRepository;
import com.wezaam.withdrawal.repository.WithdrawalScheduledRepository;
import com.wezaam.withdrawal.service.WithdrawalService;
import com.wezaam.withdrawal.model.User;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Api
@RestController
public class WithdrawalController {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private UserController userController;

    @PostMapping("/create-withdrawals")
    public ResponseEntity create(HttpServletRequest request) {
        String userId = request.getParameter("userId");
        String paymentMethodId = request.getParameter("paymentMethodId");
        String amount = request.getParameter("amount");
        String executeAt = request.getParameter("executeAt");
        if (userId == null || paymentMethodId == null || amount == null || executeAt == null) {
            return new ResponseEntity("Required params are missing", HttpStatus.BAD_REQUEST);
        }
        
        User user;
        Long userIdLong = Long.parseLong(userId);
        try {
            user = userController.findById(Long.parseLong(userId));
        } catch (Exception e) {
            return new ResponseEntity("User not found", HttpStatus.NOT_FOUND);
        }
        if (!context.getBean(PaymentMethodRepository.class).findById(Long.parseLong(paymentMethodId)).isPresent()) {
            return new ResponseEntity("Payment method not found", HttpStatus.NOT_FOUND);
        }
        if (Double.parseDouble(amount) > user.getMaxWithdrawalAmount()) {
            return new ResponseEntity("Exceeded withdrawal amount", HttpStatus.BAD_REQUEST);
        }

        WithdrawalService withdrawalService = context.getBean(WithdrawalService.class);

        WithdrawalScheduled withdrawalScheduled = new WithdrawalScheduled();
        withdrawalScheduled.setUserId(Long.parseLong(userId));
        withdrawalScheduled.setPaymentMethodId(Long.parseLong(paymentMethodId));
        withdrawalScheduled.setAmount(Double.parseDouble(amount));
        withdrawalScheduled.setCreatedAt(Instant.now());
        if (executeAt.equals("ASAP")) {
        	withdrawalScheduled.setExecuteAt(Instant.now());
        }
        else {
        	withdrawalScheduled.setExecuteAt(Instant.parse(executeAt));
        }
        withdrawalScheduled.setRetries(0L);
        withdrawalScheduled.setStatus(WithdrawalStatus.PENDING);
        withdrawalService.schedule(withdrawalScheduled);
        Object body = withdrawalScheduled;

        return new ResponseEntity(body, HttpStatus.OK);
    }

    @GetMapping("/find-all-withdrawals")
    public ResponseEntity findAll() {
        List<WithdrawalScheduled> withdrawalsScheduled = context.getBean(WithdrawalScheduledRepository.class).findAll();
        List<Object> result = new ArrayList<>();
        result.addAll(withdrawalsScheduled);

        return new ResponseEntity(result, HttpStatus.OK);
    }
    
    @GetMapping("/find-user-withdrawals/{userId}")
    public ResponseEntity findUser(@PathVariable Long userId) {
        List<WithdrawalScheduled> withdrawalsScheduled = context.getBean(WithdrawalScheduledRepository.class).findByUserId(userId);
        List<Object> result = new ArrayList<>();
        result.addAll(withdrawalsScheduled);

        return new ResponseEntity(result, HttpStatus.OK);
    }
}
