package com.wezaam.withdrawal.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;

@Entity(name = "notify")
public class Notify {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Long transactionId;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private WithdrawalStatus status;
    @Enumerated(EnumType.STRING)
    private NotifyLevel notifyLevel;
    private String notifyMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public WithdrawalStatus getStatus() {
        return status;
    }

    public void setStatus(WithdrawalStatus status) {
        this.status = status;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getTransactionId() {
        return transactionId;
    }
    
    public NotifyLevel getNotifyLevel() {
        return notifyLevel;
    }

    public void setNotifyLevel(NotifyLevel notifyLevel) {
        this.notifyLevel = notifyLevel;
    }
    
    public String getMessage() {
    	return notifyMessage;
    }
    
    public void setMessage(String notifyMessage) {
    	this.notifyMessage = notifyMessage;
    }


}
