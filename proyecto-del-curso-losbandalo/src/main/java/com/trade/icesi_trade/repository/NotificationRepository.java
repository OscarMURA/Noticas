package com.trade.icesi_trade.repository;

import com.trade.icesi_trade.model.Notification;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAll();
    Notification findByUser_Id(Long user_id);
    Optional<Notification> findById(Long id);;
}