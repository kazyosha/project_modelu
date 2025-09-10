package com.c04.productmodule.exceptionHandler;

import com.c04.productmodule.models.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {
    @ModelAttribute("currentUser")
    public User addCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
}
