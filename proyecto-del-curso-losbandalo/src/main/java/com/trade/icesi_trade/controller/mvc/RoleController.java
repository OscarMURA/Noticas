package com.trade.icesi_trade.controller.mvc;

import com.trade.icesi_trade.model.Role;
import com.trade.icesi_trade.model.Permission;
import com.trade.icesi_trade.model.RolePermission;
import com.trade.icesi_trade.repository.PermissionRepository;
import com.trade.icesi_trade.repository.RolePermissionRepository;
import com.trade.icesi_trade.Service.Interface.RoleService;
import com.trade.icesi_trade.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("permissions", permissionRepository.findAll());
        return "roles/create";
    }

    @PostMapping
    public String createRole(@ModelAttribute Role role,
                            @RequestParam(value = "permissionIds", required = false) List<Long> permissionIds) {
        
        if (permissionIds == null || permissionIds.isEmpty()) {
            return "redirect:/roles/create?error=Debe+seleccionar+al+menos+un+permiso";
        }

        if (roleRepository.findByName(role.getName()) != null) {
            return "redirect:/roles/create?error=Ya+existe+un+rol+con+ese+nombre";
        }

        role.setId(null);

        List<Permission> selectedPermissions = permissionRepository.findAllById(permissionIds);
        roleService.saveRole(role, selectedPermissions);

        return "redirect:/roles";
    }


    @GetMapping("/{id}/permissions")
    public String showAssignPermissions(@PathVariable Long id, Model model) {
        Role role = roleService.findById(id).orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        List<Permission> allPermissions = permissionRepository.findAll();
        List<Long> assignedPermissionIds = rolePermissionRepository.findByRole_Id(id)
        .stream()
        .map(rp -> rp.getPermission().getId())
        .toList();

        model.addAttribute("role", role);
        model.addAttribute("permissions", allPermissions);
        model.addAttribute("assignedPermissionIds", assignedPermissionIds);

        return "roles/assign-permissions";
    }

    @PostMapping("/{id}/permissions")
    public String updatePermissions(@PathVariable Long id,
                                    @RequestParam(value = "permissionIds", required = false) List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return "redirect:/roles/" + id + "/permissions?error=Debe+seleccionar+al+menos+un+permiso";
        }

        // Eliminar los permisos actuales
        List<RolePermission> existing = rolePermissionRepository.findByRole_Id(id);
        rolePermissionRepository.deleteAll(existing);

        // Agregar los nuevos
        Role role = roleService.findById(id).orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        for (Long pid : permissionIds) {
            RolePermission rp = RolePermission.builder()
                .role(role)
                .permission(permissionRepository.findById(pid).orElse(null))
                .build();
            rolePermissionRepository.save(rp);
        }

        return "redirect:/roles";
    }

    @GetMapping
    public String listRoles(Model model) {
        model.addAttribute("roles", roleService.findAllRoles());
        return "roles/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return "redirect:/roles";
    }


}
