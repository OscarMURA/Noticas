package com.trade.icesi_trade.Service.Interface;

import java.util.List;
import java.util.Optional;
import com.trade.icesi_trade.model.Permission;

import com.trade.icesi_trade.model.Role;

public interface RoleService {
    Role findRoleByName(String name);
    Role saveRole(Role role, List<Permission> permissions);
    Role updateRole(Long roleId, Role updatedRole);
    void deleteRole(Long roleId);
    List<Role> findAllRoles();
    List<Role> findAllById(List<Long> ids);
    Optional<Role> findById(Long id);
}
