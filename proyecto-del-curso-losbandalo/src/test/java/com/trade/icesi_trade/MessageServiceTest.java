package com.trade.icesi_trade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.trade.icesi_trade.model.Message;
import com.trade.icesi_trade.model.User;
import com.trade.icesi_trade.repository.MessageRepository;
import com.trade.icesi_trade.Service.Impl.MessageServiceImpl;

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
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    private User sender;
    private User receiver;
    private Message messageSent;
    private Message messageReceived;

    @BeforeEach
    public void setUp() {
        // Se crean los usuarios para la conversación.
        sender = User.builder().id(1L).build();
        receiver = User.builder().id(2L).build();

        // Se crea un mensaje enviado desde sender a receiver.
        messageSent = Message.builder()
                .id(1L)
                .sender(sender)
                .receiver(receiver)
                .content("Hola, ¿cómo estás?")
                .createdAt(LocalDateTime.of(2025, 4, 15, 12, 0))
                .build();

        // Se crea un segundo mensaje enviado desde sender a receiver para la prueba de getMessagesByReceiver.
        // Nota: aquí usamos sender como emisor para generar otro mensaje cuyo receptor es receiver.
        messageReceived = Message.builder()
                .id(2L)
                .sender(sender)
                .receiver(receiver)
                .content("Mensaje extra para el receptor")
                .createdAt(LocalDateTime.of(2025, 4, 15, 12, 5))
                .build();
    }

    @Test
    public void testSendMessage_Success() {
        // Arrange: Se simula que al guardar se asigna la fecha actual.
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message m = invocation.getArgument(0);
            m.setCreatedAt(LocalDateTime.now());
            return m;
        });
        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content("Mensaje de prueba")
                .build();

        // Act
        Message saved = messageService.sendMessage(message);

        // Assert
        assertNotNull(saved);
        assertNotNull(saved.getCreatedAt());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    public void testSendMessage_NullMessage() {
        // Se espera que enviar un mensaje nulo lance IllegalArgumentException.
        assertThrows(IllegalArgumentException.class, () -> messageService.sendMessage(null));
    }

    @Test
    public void testGetMessagesBySender() {
        // Arrange: Simular que se retorna el mensajeSent para el remitente sender.
        when(messageRepository.findBySender_Id(sender.getId())).thenReturn(Collections.singletonList(messageSent));

        // Act
        List<Message> messages = messageService.getMessagesBySender(sender.getId());

        // Assert
        assertNotNull(messages);
        assertEquals(1, messages.size());
        assertEquals(sender.getId(), messages.get(0).getSender().getId());
    }

    @Test
    public void testGetMessagesByReceiver() {
        // Arrange: Simular que se retornan dos mensajes cuyo receptor es receiver.
        when(messageRepository.findByReceiver_Id(receiver.getId()))
            .thenReturn(Arrays.asList(messageSent, messageReceived));

        // Act
        List<Message> messages = messageService.getMessagesByReceiver(receiver.getId());

        // Assert
        assertNotNull(messages);
        // Se esperan dos mensajes con receptor = receiver.
        assertEquals(2, messages.size());
        messages.forEach(m -> assertEquals(receiver.getId(), m.getReceiver().getId()));
    }

    @Test
    public void testGetConversation() {
        // En este caso definiremos el escenario para que la conversación combine mensajes de ambos lados.
        // Para ello, creamos dos mensajes: uno de sender hacia receiver y otro de receiver hacia sender.

        // Creamos un mensaje de sender a receiver con fecha temprana.
        Message msg1 = Message.builder()
                .id(3L)
                .sender(sender)
                .receiver(receiver)
                .content("Mensaje de sender a receiver")
                .createdAt(LocalDateTime.of(2025, 4, 15, 10, 0))
                .build();
        // Y otro mensaje de receiver a sender con fecha posterior.
        Message msg2 = Message.builder()
                .id(4L)
                .sender(receiver)
                .receiver(sender)
                .content("Respuesta de receiver a sender")
                .createdAt(LocalDateTime.of(2025, 4, 15, 10, 5))
                .build();

        // Configuramos el comportamiento del repositorio:
        when(messageRepository.findBySender_Id(sender.getId()))
            .thenReturn(Collections.singletonList(msg1));
        when(messageRepository.findBySender_Id(receiver.getId()))
            .thenReturn(Collections.singletonList(msg2));

        // Act: Se obtiene la conversación entre sender y receiver
        List<Message> conversation = messageService.getConversation(sender.getId(), receiver.getId());

        // Assert
        assertNotNull(conversation);
        // Se esperan 2 mensajes.
        assertEquals(2, conversation.size());
        // Se verifica que el mensaje con fecha más temprana esté primero.
        assertTrue(conversation.get(0).getCreatedAt().isBefore(conversation.get(1).getCreatedAt()));

        // Se verifican las llamadas al repositorio.
        verify(messageRepository, times(1)).findBySender_Id(sender.getId());
        verify(messageRepository, times(1)).findBySender_Id(receiver.getId());
    }

    @Test
    public void testGetMessagesBySender_NotFound() {
        // Si no se encuentran mensajes para el sender, se espera una lista vacía.
        when(messageRepository.findBySender_Id(sender.getId())).thenReturn(Collections.emptyList());
        List<Message> messages = messageService.getMessagesBySender(sender.getId());
        assertNotNull(messages);
        assertTrue(messages.isEmpty());
    }

    @Test
    public void testSendMessage_EmptyContent() {
        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content("   ")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                messageService.sendMessage(message));
        assertEquals("El mensaje debe tener contenido", exception.getMessage());
    }

    @Test
    public void testGetMessagesBySender_NullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                messageService.getMessagesBySender(null));
        assertEquals("El ID del remitente no puede ser nulo", exception.getMessage());
    }

    @Test
    public void testGetMessagesByReceiver_NullId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                messageService.getMessagesByReceiver(null));
        assertEquals("El ID del receptor no puede ser nulo", exception.getMessage());
    }

    @Test
    public void testGetConversation_NullSenderId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                messageService.getConversation(null, receiver.getId()));
        assertEquals("Los IDs de remitente y receptor no pueden ser nulos", exception.getMessage());
    }

    @Test
    public void testGetConversation_NullReceiverId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                messageService.getConversation(sender.getId(), null));
        assertEquals("Los IDs de remitente y receptor no pueden ser nulos", exception.getMessage());
    }

    @Test
    public void testSendMessage_NullContent() {
        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(null)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                messageService.sendMessage(message));
        assertEquals("El mensaje debe tener contenido", exception.getMessage());
    }
}