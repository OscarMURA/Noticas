package com.trade.icesi_trade.Service.Interface;

import java.util.List;
import java.util.Optional;

import com.trade.icesi_trade.model.Notification;

public interface NotificationService {
    Notification createNotification(Notification notification);
    
    Notification markNotificationAsRead(Long notificationId);
    
    List<Notification> getNotificationsByUser(Long userId);
    
    List<Notification> getPendingNotificationsByUser(Long userId);

    List<Notification> getAllNotifications();

    Optional<Notification> getNotificationById(Long id);
    
    void deleteNotification(Long id);
}
