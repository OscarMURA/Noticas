package com.trade.icesi_trade.repository;

import com.trade.icesi_trade.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findById(Long id);
    Permission findByName(String name);
    List<Permission> findAll();

}
