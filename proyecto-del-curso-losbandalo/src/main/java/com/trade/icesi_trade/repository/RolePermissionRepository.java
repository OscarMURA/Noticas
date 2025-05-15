package com.trade.icesi_trade.repository;

import com.trade.icesi_trade.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRole_Id(Long roleId);
    List<RolePermission> findByPermission_Id(Long permissionId);
    List<RolePermission> findAll();
    void deleteByRole_IdAndPermission_Id(Long roleId, Long permissionId);
}
