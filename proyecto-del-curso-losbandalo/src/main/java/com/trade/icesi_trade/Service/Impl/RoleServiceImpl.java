package com.trade.icesi_trade.Service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trade.icesi_trade.Service.Interface.RoleService;
import com.trade.icesi_trade.model.Permission;
import com.trade.icesi_trade.model.Role;
import com.trade.icesi_trade.model.RolePermission;
import com.trade.icesi_trade.model.UserRole;
import com.trade.icesi_trade.repository.PermissionRepository;
import com.trade.icesi_trade.repository.RolePermissionRepository;
import com.trade.icesi_trade.repository.RoleRepository;
import com.trade.icesi_trade.repository.UserRoleRepository;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public Role findRoleByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("El nombre del rol no puede ser nulo.");
        }
        return roleRepository.findByName(name);
    }

    @Transactional
    @Override
    public Role saveRole(Role role, List<Permission> permissions) {
        if (role.getName() == null || role.getName().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol es obligatorio.");
        }

        if (permissions == null || permissions.isEmpty()) {
            throw new IllegalArgumentException("Debe asignar al menos un permiso.");
        }

        Role savedRole = roleRepository.save(role);

        if (savedRole.getId() != null) {
            rolePermissionRepository.deleteAll(rolePermissionRepository.findByRole_Id(savedRole.getId()));
        }

        for (Permission permission : permissions) {
            RolePermission rp = RolePermission.builder()
                .role(savedRole)
                .permission(permission)
                .build();
            rolePermissionRepository.save(rp);
        }

        return savedRole;
    }

    @Transactional
    @Override
    public void deleteRole(Long roleId) {
        Role roleToDelete = roleRepository.findById(roleId)
            .orElseThrow(() -> new IllegalArgumentException("El rol no existe."));

        Role defaultRole = roleRepository.findByName("ROLE_USER");
        if (defaultRole == null) {
            throw new IllegalStateException("No se encontró el rol por defecto (ROLE_USER).");
        }

        List<UserRole> userRoles = userRoleRepository.findByRole(roleToDelete);
        for (UserRole ur : userRoles) {
            Long userId = ur.getUser().getId();

            userRoleRepository.delete(ur);

            List<UserRole> remainingRoles = userRoleRepository.findByUser_Id(userId);
            if (remainingRoles.isEmpty()) {
                userRoleRepository.save(UserRole.builder()
                    .user(ur.getUser())
                    .role(defaultRole)
                    .build());
            }
        }

        List<RolePermission> rolePermissions = rolePermissionRepository.findByRole_Id(roleId);
        if (!rolePermissions.isEmpty()) {
            rolePermissionRepository.deleteAll(rolePermissions);
        }

        roleRepository.deleteById(roleId);
    }

    @Override
    public Role updateRole(Long roleId, Role updatedRole) {
        if (roleId == null || updatedRole == null) {
            throw new IllegalArgumentException("El ID del rol y el rol actualizado no pueden ser nulos.");
        }

        if (!roleRepository.existsById(roleId)) {
            throw new IllegalArgumentException("El rol no existe.");
        }

        if (rolePermissionRepository.findByRole_Id(roleId).isEmpty()) {
            throw new IllegalArgumentException("El rol debe tener al menos un permiso asignado.");
        }

        return roleRepository.save(updatedRole);
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public List<Role> findAllById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("La lista de IDs no puede ser nula o vacía.");
        }
        return roleRepository.findAllById(ids);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }
}