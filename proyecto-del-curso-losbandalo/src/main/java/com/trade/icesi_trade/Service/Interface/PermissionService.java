package com.trade.icesi_trade.Service.Interface;

import com.trade.icesi_trade.model.Permission;

public interface PermissionService {
    Permission findPermissionByName(String name);
    Permission savePermission(Permission permission);
    void deletePermission(Long permissionId);
    Permission updatePermission(Permission permission, Long id);
}
