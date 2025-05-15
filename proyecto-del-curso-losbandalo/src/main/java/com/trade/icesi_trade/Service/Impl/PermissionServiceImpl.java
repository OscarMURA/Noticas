package com.trade.icesi_trade.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import com.trade.icesi_trade.Service.Interface.PermissionService;
import com.trade.icesi_trade.model.Permission;
import com.trade.icesi_trade.repository.PermissionRepository;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Permission findPermissionByName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("El nombre del permiso no puede ser nulo o vacío.");
        }
        Permission permission = permissionRepository.findByName(name);
        if (permission == null) {
            throw new NoSuchElementException("Permiso no encontrado.");
        }
        return permission;
    }

    @Override
    public Permission savePermission(Permission permission) {
        if (permission.getName() == null || permission.getName().isEmpty()) {
            throw new IllegalArgumentException("El permiso debe tener un nombre.");
        }
        return permissionRepository.save(permission);
    }
    

    @Override
    public void deletePermission(Long permissionId) {
        if (!permissionRepository.existsById(permissionId)) {
            throw new IllegalArgumentException("El permiso no existe.");
        }
        permissionRepository.deleteById(permissionId);
    }

    public Permission updatePermission(Permission permission, Long id) {
        if (permission == null) {
            throw new IllegalArgumentException("El permiso no puede ser nulo.");
        }
        if (id == null) {
            throw new IllegalArgumentException("El ID del permiso no puede ser nulo.");
        }
        if (permission.getName() == null || permission.getName().isEmpty()) {
            throw new IllegalArgumentException("El permiso debe tener un nombre.");
        }
    
        Permission existing = permissionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Permiso no encontrado."));
    
        existing.setName(permission.getName());
        existing.setDescription(permission.getDescription());
    
        return permissionRepository.save(existing);
    }    
}
