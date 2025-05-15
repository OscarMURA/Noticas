package com.trade.icesi_trade.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private Long typeId;
    private Boolean read;
    private Long userId;
    private String message;
    private String createdAt;
}

