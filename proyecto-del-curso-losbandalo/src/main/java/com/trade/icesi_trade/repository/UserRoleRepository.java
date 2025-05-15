package com.trade.icesi_trade.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trade.icesi_trade.model.Role;
import com.trade.icesi_trade.model.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByRole(Role role);
    List<UserRole> findByUser_Id(Long userId);
    List<UserRole> findByRole_Id(Long roleId);
    long countByUser_Id(Long userId);
}