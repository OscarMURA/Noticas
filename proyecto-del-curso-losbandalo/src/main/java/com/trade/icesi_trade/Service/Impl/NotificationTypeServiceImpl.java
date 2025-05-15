package com.trade.icesi_trade.Service.Impl;

import com.trade.icesi_trade.Service.Interface.NotificationTypeService;
import com.trade.icesi_trade.model.NotificationType;
import com.trade.icesi_trade.repository.NotificationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class NotificationTypeServiceImpl implements NotificationTypeService {

    @Autowired
    private NotificationTypeRepository repository;

    @Override
    public NotificationType findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tipo de notificación no encontrado con ID: " + id));
    }
}