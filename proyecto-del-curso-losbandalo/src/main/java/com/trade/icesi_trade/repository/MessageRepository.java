package com.trade.icesi_trade.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trade.icesi_trade.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAll();
    Optional<Message> findById(Long id);
    List<Message> findBySender_Id(Long senderId);  
    List<Message> findByReceiver_Id(Long receiverId);  
}