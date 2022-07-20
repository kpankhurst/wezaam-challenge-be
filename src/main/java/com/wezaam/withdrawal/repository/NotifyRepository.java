package com.wezaam.withdrawal.repository;

import com.wezaam.withdrawal.model.Notify;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotifyRepository extends JpaRepository<Notify, Long> {
}
