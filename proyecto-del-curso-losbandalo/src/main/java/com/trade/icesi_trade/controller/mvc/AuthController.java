package com.trade.icesi_trade.controller.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.trade.icesi_trade.Service.Impl.UserServiceImpl;
import com.trade.icesi_trade.dtos.RegisterDto;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/public")
public class AuthController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/")
    public String redirectToUsers() {
        return "redirect:/users";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(
            @Valid @ModelAttribute("registerDto") RegisterDto dto,
            BindingResult br,
            Model model
    ) {
        // validación de coincidencia de contraseñas
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            br.rejectValue("confirmPassword", "error.confirmPassword", "Las contraseñas no coinciden");
        }

        if (br.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.register(dto);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("registrationError", ex.getMessage());
            return "auth/register";
        }

        return "redirect:/login?registered";
    }

    @GetMapping("/default")
    public String redirectByRole(Authentication auth) {
        for (GrantedAuthority authority : auth.getAuthorities()) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                return "redirect:/users"; // Panel de admins
            }
        }
        return "redirect:/home"; // Panel para usuarios normales
    }
}