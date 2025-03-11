package com.example.auth_session_cookie.controller;

import com.example.auth_session_cookie.dto.EmployeeRequestDTO;
import com.example.auth_session_cookie.service.AdminService;
import com.example.auth_session_cookie.util.EmployeeValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AdminService adminService;
    private final EmployeeValidator employeeValidator;

    @Autowired
    public AuthController(AdminService adminService, EmployeeValidator employeeValidator) {
        this.adminService = adminService;
        this.employeeValidator = employeeValidator;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("employee") EmployeeRequestDTO employeeRequestDTO) {
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("employee") @Valid EmployeeRequestDTO employeeRequestDTO,
                                      BindingResult bindingResult) {
        System.out.println("Registration method called!");
        employeeValidator.validate(employeeRequestDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            return "auth/registration";
        }

        adminService.register(employeeRequestDTO);
        return "redirect:/employees/all-employees";
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "hello";
    }
}
