package com.c04.productmodule.controller;


import com.c04.productmodule.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm(HttpServletRequest request, Model model) {
        String rememberedEmail = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("rememberEmail".equals(c.getName())) {
                    rememberedEmail = c.getValue();
                }
            }
        }
        model.addAttribute("rememberedEmail", rememberedEmail);
        return "auth/login";
    }
}
