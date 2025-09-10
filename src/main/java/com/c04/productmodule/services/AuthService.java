package com.c04.productmodule.services;

import com.c04.productmodule.models.User;
import com.c04.productmodule.repositories.IAuthRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private IAuthRepository authService;
    public AuthService(IAuthRepository authService) {
        this.authService = authService;
    }

    public Optional<User> login(String email, String password) {
        return authService.findByEmail(email)
                .filter(u -> u.getPassword().equals(password));
    }
}
