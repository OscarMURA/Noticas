package com.trade.icesi_trade.Service.Impl;

import com.trade.icesi_trade.Service.Interface.MessageService;
import com.trade.icesi_trade.model.Message;
import com.trade.icesi_trade.repository.MessageRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public Message sendMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("El mensaje no puede ser nulo");
        }
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje debe tener contenido");
        }
        message.setCreatedAt(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getMessagesBySender(Long senderId) {
        if (senderId == null) {
            throw new IllegalArgumentException("El ID del remitente no puede ser nulo");
        }
        return messageRepository.findBySender_Id(senderId);
    }

    @Override
    public List<Message> getMessagesByReceiver(Long receiverId) {
        if (receiverId == null) {
            throw new IllegalArgumentException("El ID del receptor no puede ser nulo");
        }
        return messageRepository.findByReceiver_Id(receiverId);
    }

    @Override
    public List<Message> getConversation(Long senderId, Long receiverId) {
        if (senderId == null || receiverId == null) {
            throw new IllegalArgumentException("Los IDs de remitente y receptor no pueden ser nulos");
        }

        List<Message> messagesSent = messageRepository.findBySender_Id(senderId)
                .stream()
                .filter(m -> m.getReceiver() != null && m.getReceiver().getId().equals(receiverId))
                .collect(Collectors.toList());

        List<Message> messagesReceived = messageRepository.findBySender_Id(receiverId)
                .stream()
                .filter(m -> m.getReceiver() != null && m.getReceiver().getId().equals(senderId))
                .collect(Collectors.toList());

        messagesSent.addAll(messagesReceived);
        messagesSent.sort((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()));
        return messagesSent;
    }

    @Override
    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);    
    }

    @Override
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> getMessageById(Long id) {
        return messageRepository.findById(id)
                .map(List::of)
                .orElseGet(List::of);
    }
}
