package com.example.auth_session_cookie.util;

import com.example.auth_session_cookie.entity.Employee;
import com.example.auth_session_cookie.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupSeeder implements CommandLineRunner {

    private final EmployeeRepository employees;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.enabled:true}")   private boolean adminEnabled;
    @Value("${app.admin.email:admin@example.com}") private String adminEmail;
    @Value("${app.admin.password:changeMe_123!}")  private String adminPassword;
    @Value("${app.admin.name:Admin}")     private String adminName;
    @Value("${app.admin.role:ROLE_ADMIN}")private String adminRole;

    @Value("${app.user.enabled:true}")    private boolean userEnabled;
    @Value("${app.user.email:user@example.com}") private String userEmail;
    @Value("${app.user.password:User123!}")      private String userPassword;
    @Value("${app.user.name:User}")       private String userName;
    @Value("${app.user.role:ROLE_USER}")  private String userRole;

    @Transactional
    @Override
    public void run(String... args) {
        if (adminEnabled) {
            ensureAccount(adminEmail, adminName, adminRole, adminPassword);
        } else {
            log.info("Admin seeding disabled.");
        }

        if (userEnabled) {
            ensureAccount(userEmail, userName, userRole, userPassword);
        } else {
            log.info("User seeding disabled.");
        }
    }

    private void ensureAccount(String email, String name, String role, String rawPassword) {
        employees.findByEmail(email).ifPresentOrElse(existing -> {
            if (!role.equals(existing.getRole())) {
                existing.setRole(role);
                employees.save(existing);
                log.warn("User '{}' existed; role set to {}.", email, role);
            } else {
                log.info("User '{}' already exists with role {}.", email, role);
            }
        }, () -> {
            Employee e = new Employee();
            e.setEmployeeName(name);
            e.setEmail(email);
            e.setPassword(passwordEncoder.encode(rawPassword));
            e.setRole(role);

            e.setPosition("Seeded");
            e.setAddress("N/A");

            employees.save(e);
            log.warn("Default user '{}' with role {} created.", email, role);
        });
    }
}

