package com.trade.icesi_trade.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Entity
@Table(name = "FAVORITES_PRODUCTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteProduct {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product")
    private Product product;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}