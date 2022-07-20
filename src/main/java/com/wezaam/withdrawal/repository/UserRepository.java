package com.wezaam.withdrawal.repository;

import com.wezaam.withdrawal.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
