package com.trade.icesi_trade.Service.Impl;

import com.trade.icesi_trade.Service.Interface.NotificationService;
import com.trade.icesi_trade.model.Notification;
import com.trade.icesi_trade.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Creates a new notification and saves it to the repository.
     * 
     * @param notification The notification object to be created. Must not be null.
     *                     If the creation timestamp is not provided, it will be set
     *                     to the current date and time. If the read status is not
     *                     specified, it will default to false (unread).
     * @return The saved notification object.
     * @throws IllegalArgumentException If the provided notification is null.
     */
    @Override
    public Notification createNotification(Notification notification) {
        if (notification == null) {
            throw new IllegalArgumentException("La notificación no puede ser nula.");
        }
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }
        // Si no se especifica el estado de lectura, se asume que es no leída (false)
        if (notification.getRead() == null) {
            notification.setRead(false);
        }
        return notificationRepository.save(notification);
    }

    /**
     * Marks a notification as read by its ID.
     *
     * @param notificationId the ID of the notification to be marked as read.
     * @return the updated Notification object after being marked as read.
     * @throws IllegalArgumentException if the notificationId is null.
     * @throws NoSuchElementException if no notification is found with the given ID.
     */
    @Override
    public Notification markNotificationAsRead(Long notificationId) {
        if (notificationId == null) {
            throw new IllegalArgumentException("El ID de la notificación no puede ser nulo.");
        }
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NoSuchElementException("Notificación no encontrada con el ID: " + notificationId));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    /**
     * Retrieves a list of notifications associated with a specific user.
     *
     * @param userId the ID of the user whose notifications are to be retrieved.
     *               Must not be null.
     * @return a list of {@link Notification} objects associated with the specified user.
     * @throws IllegalArgumentException if the provided userId is null.
     */
    @Override
    public List<Notification> getNotificationsByUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo.");
        }

        return notificationRepository.findAll()
        .stream()
        .filter(n -> n != null && n.getUser() != null && n.getUser().getId().equals(userId))
        .collect(Collectors.toList());

    }

    /**
     * Retrieves a list of pending notifications for a specific user.
     * A notification is considered pending if it has not been read 
     * (i.e., its "read" property is null or false).
     *
     * @param userId the ID of the user whose pending notifications are to be retrieved
     * @return a list of pending notifications for the specified user
     */
    @Override
    public List<Notification> getPendingNotificationsByUser(Long userId) {
        List<Notification> allNotifications = getNotificationsByUser(userId);
        return allNotifications.stream()
                .filter(n -> n.getRead() == null || !n.getRead())
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    @Override
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
