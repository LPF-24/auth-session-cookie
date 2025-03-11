package com.example.auth_session_cookie.controller;

import com.example.auth_session_cookie.dto.CodeRequestDTO;
import com.example.auth_session_cookie.dto.EmployeeUpdateDTO;
import com.example.auth_session_cookie.dto.UserUpdatePasswordDTO;
import com.example.auth_session_cookie.security.EmployeeDetails;
import com.example.auth_session_cookie.service.AdminService;
import com.example.auth_session_cookie.service.EmployeeService;
import com.example.auth_session_cookie.util.EmployeeValidator;
import com.example.auth_session_cookie.util.PasswordDoesNotMatchException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final AdminService adminService;
    private final EmployeeValidator employeeValidator;

    @Autowired
    public EmployeeController(EmployeeService employeeService, AdminService adminService, EmployeeValidator employeeValidator) {
        this.employeeService = employeeService;
        this.adminService = adminService;
        this.employeeValidator = employeeValidator;
    }

    @GetMapping("/all-employees")
    public String allEmployees(Model model, Authentication authentication) {
        EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
        int employeeId = employeeDetails.getId();

        model.addAttribute("employee", employeeService.getEmployeeById(employeeId));
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("isAdmin", isAdmin(authentication));

        return "employees/index";
    }

    @GetMapping("/{id}/profile")
    public String showEmployeeProfile(Model model, Authentication authentication) {
        EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
        int employeeId = employeeDetails.getId();

        model.addAttribute("employee", employeeService.getEmployeeById(employeeId));
        model.addAttribute("tasks", employeeService.getTasksById(employeeId));
        model.addAttribute("isAdmin", isAdmin(authentication));

        return "employees/profile";
    }

    @GetMapping("/{id}/edit-employee")
    public String updateEmployeeForm(@PathVariable("id") int id, Model model, Authentication authentication) {
        EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
        Integer employeeId = employeeDetails.getId();

        if (isAdmin(authentication) || id == employeeId) {
            model.addAttribute("employee", employeeService.getEmployeeById(id));
        } else {
            throw new AccessDeniedException("You do not have permission to edit this employee.");
        }

        return "employees/edit-employee";
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String editEmployee(@PathVariable("id") int id,
                               @ModelAttribute("employee") @Valid EmployeeUpdateDTO dto,
                               BindingResult bindingResult, Authentication authentication) {
        employeeValidator.validate(dto, bindingResult);

        if (bindingResult.hasErrors()) {
            return "employees/edit-employee";
        }

        employeeService.updateEmployee(id, dto);

        EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
        Integer employeeId = employeeDetails.getId();
        if (id == employeeId) {
            return "redirect:/employees/" + id + "/profile";
        } else {
            return "redirect:/employees/" + id + "/show";
        }
    }

    @GetMapping("/{id}/show")
    public String showEmployeeInfo(@PathVariable("id") int id, Model model, Authentication authentication) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        model.addAttribute("isAdmin", isAdmin(authentication));
        model.addAttribute("tasks", employeeService.getTasksById(id));

        return "employees/info-employee";
    }

    @RequestMapping(value = "/{id}/delete-employee", method = {RequestMethod.DELETE, RequestMethod.POST})
    public String deleteEmployee(@PathVariable("id") int employeeId, Authentication authentication) {

        EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
        Integer currentEmployeeId = employeeDetails.getId();

        adminService.deleteEmployee(employeeId);
        if (currentEmployeeId.equals(employeeId)) {
            return "redirect:/auth/login";
        }
        return "redirect:/employees/all-employees";
    }

    @GetMapping("/{id}/update-password")
    public String updatePasswordForm(@ModelAttribute("user") UserUpdatePasswordDTO dto) {
        return "employees/update-password";
    }

    @RequestMapping(value = "/{id}/update-password", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String updateEmployeePassword(@ModelAttribute("user") @Valid UserUpdatePasswordDTO dto,
                                         BindingResult bindingResult,
                                         Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "employees/update-password";
        }

        EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();

        try {
            employeeService.updatePassword(employeeDetails.getId(), dto);
        } catch (PasswordDoesNotMatchException ex) {
            bindingResult.rejectValue(
                    "oldPassword",
                    "oldPasswordMismatch",
                    ex.getMessage()
            );

            return "employees/update-password";
        }
        return "redirect:/employees/" + employeeDetails.getId() + "/profile";
    }

    @GetMapping("/{id}/admin-code")
    public String codeForm(@ModelAttribute("codeDTO") CodeRequestDTO codeRequestDTO,
                           Model model, Authentication authentication) {
        model.addAttribute("isAdmin", isAdmin(authentication));

        return "employees/admin-code";
    }

    @RequestMapping(value = "/{id}/admin-code", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String verifyCodeAndPromoteEmployee(
            @Valid @ModelAttribute("codeDTO") CodeRequestDTO codeRequestDTO,
            BindingResult bindingResult,
            Authentication authentication) {

        if (bindingResult.hasErrors()) {
            return "employees/admin-code";
        }

        EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
        Integer employeeId = employeeDetails.getId();

        try {
            employeeService.promoteEmployee(employeeId);
        } catch (EntityNotFoundException ex) {
            bindingResult.rejectValue(
                    "code",
                    "Invalid code",
                    ex.getMessage()
            );
            return "employees/admin-code";
        }

        return "redirect:/auth/login";
    }

    private static boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return false;
        }

        EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();

        return employeeDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
