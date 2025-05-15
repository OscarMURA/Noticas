package com.trade.icesi_trade.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Long id;
    private Integer rating;
    private String comment;
    private Long productId;
    private Long reviewerId;
    private Long revieweeId;
    private String createdAt;
}
