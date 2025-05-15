package com.trade.icesi_trade.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private String title;
    private String description;
    private String status;
    private Double price;
    private String location;
    private Boolean isSold;
    private Long categoryId;
    private Long sellerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
