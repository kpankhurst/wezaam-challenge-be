package com.wezaam.withdrawal.service;

import com.wezaam.withdrawal.model.Withdrawal;
import com.wezaam.withdrawal.model.WithdrawalScheduled;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.context.annotation.Bean;

@Service
public class EventsService {
    @Autowired
    private ApplicationContext context;
	@Autowired
	JmsTemplate jmsTemplate;
    
    @Async
    public void send(WithdrawalScheduled withdrawal) {
        // build and send an event in message queue async
//        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        jmsTemplate.convertAndSend("payments", withdrawal);

    }
}
