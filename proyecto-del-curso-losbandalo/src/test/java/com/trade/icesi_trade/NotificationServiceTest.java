package com.trade.icesi_trade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.trade.icesi_trade.model.Notification;
import com.trade.icesi_trade.model.User;
import com.trade.icesi_trade.repository.NotificationRepository;
import com.trade.icesi_trade.Service.Impl.NotificationServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    
    @InjectMocks
    private NotificationServiceImpl notificationService;
    
    private Notification defaultNotification;
    private Notification secondNotification;
    private List<Notification> notificationList;
    
    @BeforeEach
    public void setUp() {
        defaultNotification = Notification.builder()
                .id(1L)
                .read(false)
                .message("Mensaje recibido")
                .user(User.builder().id(10L).build())
                .build();
        
        secondNotification = Notification.builder()
                .id(2L)
                .read(true)
                .message("Oferta aceptada")
                .user(User.builder().id(10L).build())
                .build();
        
        notificationList = Arrays.asList(defaultNotification, secondNotification);
    }

        
    @BeforeEach
    public void setUpNotificationList() {
        notificationList = Arrays.asList(defaultNotification, secondNotification);
    }
    
    /**
     * Test case for the successful creation of a notification.
     * 
     * This test verifies that the NotificationService correctly creates a 
     * notification and saves it using the NotificationRepository. It ensures 
     * that the created notification has the expected message, the read status 
     * is initialized to false, and the repository's save method is called once.
     */
    @Test
    public void testCreateNotification_Success() {
        // Arrange
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        Notification created = notificationService.createNotification(defaultNotification);
        
        // Assert
        assertNotNull(created);
        assertEquals("Mensaje recibido", created.getMessage());
        assertFalse(created.getRead());
        verify(notificationRepository, times(1)).save(defaultNotification);
    }
    
    /**
     * Tests the behavior of the createNotification method when a null notification is provided.
     * Verifies that an IllegalArgumentException is thrown with the expected message.
     */
    @Test
    public void testCreateNotification_NullNotification() {
        // Act  Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.createNotification(null);
        });
        assertEquals("La notificación no puede ser nula.", exception.getMessage());
    }
    
    @Test
    public void testMarkNotificationAsRead_Success() {
        // Arrange
        when(notificationRepository.findById(1L))
                .thenReturn(Optional.of(defaultNotification));
        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        Notification updated = notificationService.markNotificationAsRead(1L);
        
        // Assert
        assertNotNull(updated);
        assertTrue(updated.getRead());
        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).save(defaultNotification);
    }
    
    /**
     * Tests the behavior of the NotificationService when attempting to mark a 
     * notification as read that does not exist in the repository.
     * 
     * This test verifies that a NoSuchElementException is thrown with the 
     * appropriate error message when the notification ID is not found.
     */
    @Test
    public void testMarkNotificationAsRead_NotFound() {
        // Arrange
        when(notificationRepository.findById(1L))
                .thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            notificationService.markNotificationAsRead(1L);
        });
        assertTrue(exception.getMessage().contains("Notificación no encontrada con el ID: 1"));
    }
    
    /**
     * Test case for the NotificationService's getNotificationsByUser method.
     * 
     * This test verifies that the method correctly retrieves notifications
     * associated with a specific user ID. It ensures that:
     * - The returned list is not null.
     * - The size of the list matches the expected number of notifications.
     * - All notifications in the list belong to the specified user ID.
     * - The notificationRepository's findAll method is called exactly once.
     */
    @Test
    public void testGetNotificationsByUser_Success() {
        // Arrange
        when(notificationRepository.findAll()).thenReturn(notificationList);
        
        // Act
        List<Notification> notifications = notificationService.getNotificationsByUser(10L);
        
        // Assert
        assertNotNull(notifications);
        assertEquals(2, notifications.size());
        List<Long> userIds = notifications.stream().map(n -> n.getUser().getId()).collect(Collectors.toList());
        assertTrue(userIds.stream().allMatch(id -> id.equals(10L)));
        verify(notificationRepository, times(1)).findAll();
    }
    
    /**
     * Tests the behavior of the getNotificationsByUser method when a null userId is provided.
     * Verifies that an IllegalArgumentException is thrown with the expected error message.
     */
    @Test
    public void testGetNotificationsByUser_NullUserId() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.getNotificationsByUser(null);
        });
        assertEquals("El ID del usuario no puede ser nulo.", exception.getMessage());
    }
    
    /**
     * Test for getPendingNotificationsByUser method.
     * Verifies that only unread notifications for a specific user are returned.
     */
    @Test
    public void testGetPendingNotificationsByUser_Success() {
        // Arrange
        when(notificationRepository.findAll()).thenReturn(notificationList);
        
        // Act
        List<Notification> pending = notificationService.getPendingNotificationsByUser(10L);
        
        // Asser: Only expect defaultNotification to be pending (read false)
        assertNotNull(pending);
        assertEquals(1, pending.size());
        assertFalse(pending.get(0).getRead());
        verify(notificationRepository, times(1)).findAll();
    }

    /**
     * Tests the behavior of markNotificationAsRead when a null ID is provided.
     * Expects IllegalArgumentException.
     */
    @Test
    public void testMarkNotificationAsRead_NullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.markNotificationAsRead(null);
        });

        assertEquals("El ID de la notificación no puede ser nulo.", exception.getMessage());
    }

    /**
     * Tests getNotificationsByUser when the user has no notifications.
     */
    @Test
    public void testGetNotificationsByUser_EmptyList() {
        when(notificationRepository.findAll()).thenReturn(List.of());

        List<Notification> result = notificationService.getNotificationsByUser(99L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(notificationRepository).findAll();
    }

    /**
     * Tests getPendingNotificationsByUser when all notifications are already read.
     */
    @Test
    public void testGetPendingNotificationsByUser_NonePending() {
        Notification read1 = Notification.builder()
                .id(3L)
                .read(true)
                .message("Leída 1")
                .user(User.builder().id(20L).build())
                .build();

        Notification read2 = Notification.builder()
                .id(4L)
                .read(true)
                .message("Leída 2")
                .user(User.builder().id(20L).build())
                .build();

        when(notificationRepository.findAll()).thenReturn(List.of(read1, read2));

        List<Notification> result = notificationService.getPendingNotificationsByUser(20L);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(notificationRepository).findAll();
    }

    @Test
    public void testCreateNotification_SetsCreatedAtIfNull() {
        Notification not = Notification.builder()
            .id(5L)
            .message("Sin fecha")
            .user(User.builder().id(3L).build())
            .build(); // createdAt es null por defecto

        when(notificationRepository.save(any(Notification.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Notification result = notificationService.createNotification(not);
        assertNotNull(result.getCreatedAt(), "createdAt debería haberse inicializado automáticamente");
    }

    @Test
    public void testCreateNotification_SetsReadFalseIfNull() {
        Notification not = Notification.builder()
            .id(6L)
            .message("Sin estado de lectura")
            .createdAt(LocalDateTime.now())
            .user(User.builder().id(3L).build())
            .build(); // read es null

        when(notificationRepository.save(any(Notification.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Notification result = notificationService.createNotification(not);
        assertFalse(result.getRead(), "El estado 'read' debe ser false por defecto");
    }


}
