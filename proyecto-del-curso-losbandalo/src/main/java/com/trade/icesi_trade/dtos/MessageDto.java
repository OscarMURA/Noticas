package com.trade.icesi_trade.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private Long id;
    private String content;
    private Long senderId;
    private Long receiverId;
    private String createdAt;
}