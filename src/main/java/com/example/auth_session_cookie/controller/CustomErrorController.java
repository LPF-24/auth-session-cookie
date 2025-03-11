package com.example.auth_session_cookie.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

@Controller
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {
    @GetMapping()
    public String handleError(HttpServletRequest request, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/auth/login";
        }

        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String errorMessage = (String) request.getAttribute("jakarta.servlet.error.message");
        String path = (String) request.getAttribute("jakarta.servlet.error.request_uri");

        model.addAttribute("status", Objects.requireNonNullElse(statusCode, "Unknown error"));
        model.addAttribute("error", Objects.requireNonNullElse(errorMessage, "No message available"));
        model.addAttribute("path", Objects.requireNonNullElse(path, "Unknown path"));

        return "error";
    }
}
