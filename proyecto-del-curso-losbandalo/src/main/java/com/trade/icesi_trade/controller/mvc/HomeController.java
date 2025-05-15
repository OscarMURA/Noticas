package com.trade.icesi_trade.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/home")
    public String userHome() {
        return "home/user-home";
    }
}
