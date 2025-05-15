package com.trade.icesi_trade.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ROLE_PERMISSIONS", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"role", "permission"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "role", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "permission", nullable = false)
    private Permission permission;
}