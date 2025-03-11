package com.example.auth_session_cookie.controller;

import com.example.auth_session_cookie.dto.EmployeeResponseDTO;
import com.example.auth_session_cookie.dto.TaskRequestDTO;
import com.example.auth_session_cookie.dto.TaskResponseDTO;
import com.example.auth_session_cookie.security.EmployeeDetails;
import com.example.auth_session_cookie.service.AdminService;
import com.example.auth_session_cookie.service.EmployeeService;
import com.example.auth_session_cookie.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final AdminService adminService;
    private final EmployeeService employeeService;
    private final TaskService taskService;

    @Autowired
    public TaskController(AdminService adminService, EmployeeService employeeService, TaskService taskService) {
        this.adminService = adminService;
        this.employeeService = employeeService;
        this.taskService = taskService;
    }

    @GetMapping()
    public String showAllTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());

        return "tasks/all-tasks";
    }

    @GetMapping("/{id}/task")
    public String showTask(@PathVariable("id") int id, Model model, Authentication authentication) {
        TaskResponseDTO task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        model.addAttribute("isExpired", taskService.isExpired(task.getDeadline()));

        boolean isAdmin = isAdmin(authentication);
        model.addAttribute("isAdmin", isAdmin);

        if (isAdmin) {
            EmployeeResponseDTO executor = taskService.getEmployeeByTaskId(id);

            if (executor == null) {
                model.addAttribute("employees", employeeService.getAllEmployees());
            } else {
                model.addAttribute("executor", executor);
            }
        }

        return "tasks/show-task";
    }

    @RequestMapping(value = "/{id}/assign", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String assignTask(@PathVariable("id") int id, RedirectAttributes redirectAttributes,
                             @RequestParam("employeeId") Integer employeeId) {
        adminService.assignTask(id, employeeId);
        redirectAttributes.addFlashAttribute("executor", taskService.getEmployeeByTaskId(id));

        return "redirect:/tasks/" + id + "/task";
    }

    @RequestMapping(value = "/{id}/release", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String releaseTask(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        adminService.releaseTask(id);

        return "redirect:/tasks/" + id + "/task";
    }

    @GetMapping("/add-task")
    public String newTask(@ModelAttribute("task") TaskRequestDTO dto, Model model, Authentication authentication) {
        model.addAttribute("employees", employeeService.getAllEmployees());

        EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
        model.addAttribute("adminId", employeeDetails.getId());

        return "tasks/create-task";
    }

    @PostMapping()
    public String createTask(@ModelAttribute("task") @Valid TaskRequestDTO taskRequestDTO,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("employees", employeeService.getAllEmployees());
            return "tasks/create-task";
        }

        adminService.createTask(taskRequestDTO);

        return "redirect:/tasks";
    }

    @GetMapping("/{id}/edit-task")
    public String showUpdateTaskForm(@PathVariable("id") int id, Model model) {
        TaskResponseDTO task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        model.addAttribute("taskId", id);

        return "tasks/edit-task";
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public String updateTask(@PathVariable("id") int id, @ModelAttribute("task") @Valid TaskRequestDTO taskRequestDTO,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("taskId", id);
            model.addAttribute("task", taskRequestDTO);
            return "tasks/edit-task";
        }

        adminService.updateTask(id, taskRequestDTO);
        return "redirect:/tasks/" + id + "/task";
    }

    @RequestMapping(value = "/{id}/delete-task", method = {RequestMethod.DELETE, RequestMethod.POST})
    public String deleteTask(@PathVariable("id") int taskId) {
        adminService.deleteTask(taskId);
        return "redirect:/tasks";
    }

    @GetMapping("/search-tasks")
    public String searchPage(Model model, Authentication authentication) {
        EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
        model.addAttribute("adminId", employeeDetails.getId());
        return "tasks/search-page";
    }

    @GetMapping("/search")
    public String makeSearch(@RequestParam(value = "query", required = false) String query,
                             Model model, Authentication authentication) {
        EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
        model.addAttribute("adminId", employeeDetails.getId());

        if (query != null && !query.isBlank()) {
            model.addAttribute("tasks", adminService.searchTasksBySubject(query));
        }

        return "tasks/search-page";
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
