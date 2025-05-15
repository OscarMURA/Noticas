package com.trade.icesi_trade.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TYPE_NOTIFICATION")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationType {
    @Id
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;
}