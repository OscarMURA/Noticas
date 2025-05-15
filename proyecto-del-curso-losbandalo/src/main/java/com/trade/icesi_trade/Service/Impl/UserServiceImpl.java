package com.trade.icesi_trade.Service.Impl;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.trade.icesi_trade.Service.Interface.UserService;
import com.trade.icesi_trade.dtos.RegisterDto;
import com.trade.icesi_trade.model.Role;
import com.trade.icesi_trade.model.User;
import com.trade.icesi_trade.model.UserRole;
import com.trade.icesi_trade.repository.UserRepository;
import com.trade.icesi_trade.repository.UserRoleRepository;

import jakarta.transaction.Transactional;
  
@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private  UserRoleRepository userRoleRepository;

    @Autowired
    private  RoleServiceImpl roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado."));
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado."));
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User saveUser(User user) {
        if (user.getId() == null || user.getEmail() == null) {
            throw new IllegalArgumentException("El usuario debe tener al menos un ID y un email.");
        }
        if (userRoleRepository.countByUser_Id(user.getId()) == 0) {
            throw new IllegalArgumentException("El usuario debe tener al menos un rol asignado.");
        }

        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);

        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("El usuario no existe.");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public User updateUser(User user, Long id) {
        if (user.getId() == null || user.getEmail() == null) {
            throw new IllegalArgumentException("El usuario debe tener al menos un ID y un email.");
        }
        User existingUser = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado."));
        existingUser.setEmail(user.getEmail());
        existingUser.setName(user.getName());
        existingUser.setPhone(user.getPhone());
        existingUser.setPassword(user.getPassword());
        existingUser.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(existingUser);
    }

    @Transactional
    public void updateUserRoles(Long userId, List<Long> newRoleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        List<UserRole> existingRoles = userRoleRepository.findByUser_Id(userId);
        userRoleRepository.deleteAllInBatch(existingRoles); 
    
        List<Long> distinctIds = newRoleIds.stream().distinct().toList();
    
        List<Role> newRoles = roleService.findAllById(distinctIds);
        for (Role role : newRoles) {
            UserRole ur = UserRole.builder()
                    .user(user)
                    .role(role)
                    .build();
            userRoleRepository.save(ur);
        }
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email, null));
        List<UserRole> roles = user.getUserRoles();

        List<GrantedAuthority> auths = new ArrayList<>();
        if(roles != null && !roles.isEmpty()) {
            auths = roles.stream()
                    .map(userRole -> (GrantedAuthority)() -> userRole.getRole().getName())
                    .toList();
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(auths)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();

        return userDetails;
    }

    @Override
    @Transactional
    public User register(RegisterDto dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }
        User u = new User();
        u.setEmail(dto.getEmail());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setName(dto.getName());
        u.setPhone(dto.getPhone());
        u.setCreatedAt(LocalDateTime.now());
        User saved = userRepository.save(u);

        // 2) asigno “ROLE_USER”
        Role userRole = roleService.findRoleByName("ROLE_USER");
        UserRole ur = UserRole.builder()
                            .user(saved)
                            .role(userRole)
                            .build();
        userRoleRepository.save(ur);

        return saved;
    }
    
}