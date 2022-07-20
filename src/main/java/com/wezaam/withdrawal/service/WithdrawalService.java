package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.exception.TransactionException;
import com.wezaam.withdrawal.model.PaymentMethod;
import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.model.WithdrawalStatus;
import com.wezaam.withdrawal.repository.PaymentMethodRepository;
import com.wezaam.withdrawal.repository.NotifyRepository;
import com.wezaam.withdrawal.repository.WithdrawalScheduledRepository;
import com.wezaam.withdrawal.model.Notify;
import com.wezaam.withdrawal.model.NotifyLevel;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.ArrayList;

@Service
public class WithdrawalService {

    @Autowired
    private NotifyRepository notifyRepository;
    @Autowired
    private WithdrawalScheduledRepository withdrawalScheduledRepository;
    @Autowired
    private WithdrawalProcessingService withdrawalProcessingService;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private EventsService eventsService;
    @Autowired
    private NotifyService notifyService;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void schedule(WithdrawalScheduled withdrawalScheduled) {
        withdrawalScheduledRepository.save(withdrawalScheduled);
    }

    @Scheduled(fixedDelay = 5000)
    public void run() {
    	List<WithdrawalStatus> statusList = new ArrayList<WithdrawalStatus>();
    	statusList.add(WithdrawalStatus.PENDING);
    	statusList.add(WithdrawalStatus.FAILED); 
     	// statusList.add(WithdrawalStatus.INTERNAL_ERROR); - We will consider an INTERNAL_ERROR as a terminal error 
    	
        withdrawalScheduledRepository.findAllByExecuteAtBeforeAndStatusIn(Instant.now(), statusList)
                .forEach(this::processScheduled);
    }

    private void processScheduled(WithdrawalScheduled withdrawal) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(withdrawal.getPaymentMethodId()).orElse(null);
        if (paymentMethod != null) {
            try {
                var transactionId = withdrawalProcessingService.sendToProcessing(withdrawal.getAmount(), paymentMethod);
                withdrawal.setStatus(WithdrawalStatus.PROCESSING);
                withdrawal.setTransactionId(transactionId);
                withdrawalScheduledRepository.save(withdrawal);
                eventsService.send(withdrawal);
            } catch (Exception e) {
                // Set executeAt to now + value
                withdrawal.incrementRetries();

            	if (e instanceof TransactionException && withdrawal.getRetries() <= 5) {
                    withdrawal.setExecuteAt(Instant.now().plus(30 * withdrawal.getRetries(), ChronoUnit.SECONDS));
            		withdrawal.setStatus(WithdrawalStatus.FAILED);
                    notifyService.notify(withdrawal, NotifyLevel.WARNING, "Failure to send. Transaction exception received.");
                } else {
                	
                    withdrawal.setStatus(WithdrawalStatus.INTERNAL_ERROR);
                	notifyService.notify(withdrawal, NotifyLevel.ERROR, "Failure to send. Internal Error.");
                }
                withdrawalScheduledRepository.save(withdrawal);
            }
        }
    }
}
