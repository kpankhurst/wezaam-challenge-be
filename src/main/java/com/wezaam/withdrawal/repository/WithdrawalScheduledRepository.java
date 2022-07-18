package com.wezaam.withdrawal.repository;

import com.wezaam.withdrawal.model.WithdrawalScheduled;
import com.wezaam.withdrawal.model.WithdrawalStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface WithdrawalScheduledRepository extends JpaRepository<WithdrawalScheduled, Long> {

/*	@Query(value = "SELECT * FROM scheduled_withdrawals WHERE executeAt < ?1 AND status = ?2", nativeQuery = true)
    List<WithdrawalScheduled> findAllByExecuteAtBefore(Instant date, String status);
    */

    List<WithdrawalScheduled> findAllByExecuteAtBefore(Instant date);
    List<WithdrawalScheduled> findAllByExecuteAtBeforeAndStatus(Instant date, WithdrawalStatus status);
    List<WithdrawalScheduled> findAllByExecuteAtBeforeAndStatusIn(Instant date, List<WithdrawalStatus> status);

}
