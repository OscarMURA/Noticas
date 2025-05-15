package com.trade.icesi_trade.controller.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import com.trade.icesi_trade.Service.Interface.UserService;
import com.trade.icesi_trade.model.Role;
import com.trade.icesi_trade.model.User;
import com.trade.icesi_trade.repository.UserRoleRepository;
import com.trade.icesi_trade.Service.Interface.RoleService;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private  UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleRepository userRoleRepository;

 
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "users/list";  
    }
    
    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail( String email) {
        User user =  userService.findUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser =  userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "User with ID " + id + " deleted successfully.";
    }

    @GetMapping("/{id}/roles")
    public String showAssignRolesForm(@PathVariable Long id, Model model) {
        User user = userService.findUserById(id);
        List<Role> allRoles = roleService.findAllRoles();

        List<Long> assignedRoleIds = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getId())
                .toList();

        model.addAttribute("user", user);
        model.addAttribute("roles", allRoles);
        model.addAttribute("assignedRoleIds", assignedRoleIds);
        return "users/assign-roles";
    }

    @PostMapping("/{id}/roles")
    public String updateRoles(@PathVariable Long id,
                          @RequestParam(value = "roleIds", required = false) List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return "redirect:/users/" + id + "/roles?error=Debe+asignar+al+menos+un+rol";
        }

        userService.updateUserRoles(id, roleIds);
        return "redirect:/users";
    }
}
