package com.trade.icesi_trade.controller.api;

import com.trade.icesi_trade.Service.Interface.MessageService;
import com.trade.icesi_trade.Service.Interface.UserService;
import com.trade.icesi_trade.dtos.MessageDto;
import com.trade.icesi_trade.mappers.MessageMapper;
import com.trade.icesi_trade.model.Message;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "CRUD operations for messages")
public class MessageApiController {
    @Autowired
    private final MessageService messageService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final MessageMapper messageMapper;

    @Operation(summary = "Get all messages")
    @GetMapping
    public ResponseEntity<List<MessageDto>> getAllMessages() {
        List<MessageDto> messages = messageService.getAllMessages().stream()
                .map(messageMapper::entityToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "Get message by ID")
    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> getMessageById(@PathVariable Long id) {
        return messageService.getMessageById(id).stream()
                .findFirst()
                .map(messageMapper::entityToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get messages by sender ID")
    @PostMapping
    public ResponseEntity<MessageDto> createMessage(@RequestBody MessageDto dto) {
        Message message = messageMapper.dtoToEntity(dto);
        message.setSender(userService.findUserById(dto.getSenderId()));
        message.setReceiver(userService.findUserById(dto.getReceiverId()));
        message.setCreatedAt(LocalDateTime.now());

        Message saved = messageService.sendMessage(message);
        return ResponseEntity.status(201).body(messageMapper.entityToDto(saved));
    }

    @Operation(summary = "Update message by ID")
    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> updateMessage(@PathVariable Long id, @RequestBody MessageDto dto) {
        Message message = messageMapper.dtoToEntity(dto);
        message.setId(id);
        message.setSender(userService.findUserById(dto.getSenderId()));
        message.setReceiver(userService.findUserById(dto.getReceiverId()));
        message.setCreatedAt(LocalDateTime.now());

        Message updated = messageService.sendMessage(message);
        return ResponseEntity.ok(messageMapper.entityToDto(updated));
    }

    @Operation(summary = "Delete message by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}