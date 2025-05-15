package com.trade.icesi_trade.Service.Interface;

import java.util.List;
import com.trade.icesi_trade.model.Message;

public interface MessageService {
    List<Message> getAllMessages();

    List<Message> getMessageById(Long id);

    void deleteMessage(Long id);

    // Envía un mensaje y lo guarda en la base de datos
    Message sendMessage(Message message);

    // Recupera el listado de mensajes enviados por un usuario
    List<Message> getMessagesBySender(Long senderId);

    // Recupera el listado de mensajes recibidos por un usuario
    List<Message> getMessagesByReceiver(Long receiverId);

    // Recupera la conversación entre dos usuarios (mensajes enviados en ambos sentidos)
    List<Message> getConversation(Long senderId, Long receiverId);
}
