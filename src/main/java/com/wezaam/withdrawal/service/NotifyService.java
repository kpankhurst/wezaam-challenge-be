package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.repository.NotifyRepository;
import com.wezaam.withdrawal.model.Notify;
import com.wezaam.withdrawal.model.NotifyLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class NotifyService {

    @Autowired
    private NotifyRepository notifyRepository;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void notify(WithdrawalScheduled withdrawal, NotifyLevel level, String message) {
                    Notify notify = new Notify();
                    notify.setUserId(withdrawal.getUserId());
                    notify.setStatus(withdrawal.getStatus());
                    notify.setTransactionId(withdrawal.getTransactionId());
                    notify.setNotifyLevel(NotifyLevel.ERROR);
                	notify.setMessage(message);
                    notifyRepository.save(notify);
    }
}
