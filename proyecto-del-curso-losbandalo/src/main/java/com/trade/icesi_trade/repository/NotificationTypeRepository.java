package com.trade.icesi_trade.repository;

import com.trade.icesi_trade.model.NotificationType;

import java.util.Optional;

import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {

    Optional<NotificationType> findById(Long notificationType_id);
    NotificationType findByName(VarcharJdbcType name);
}