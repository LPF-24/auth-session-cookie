package com.example.auth_session_cookie.config;

import com.example.auth_session_cookie.security.EmployeeDetails;
import jakarta.servlet.SessionCookieConfig;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/employees/{id}/profile").access((authentication, request) -> {
                            String requestUri = request.getRequest().getRequestURI();
                            String userId = requestUri.replaceAll("\\D+", "");

                            EmployeeDetails employeeDetails = (EmployeeDetails) authentication.get().getPrincipal();
                            String currentUserId = String.valueOf(employeeDetails.getId());

                            boolean isOwner = userId.equals(currentUserId);

                            return new AuthorizationDecision(isOwner);
                        })
                        .requestMatchers("/auth/login", "/error").permitAll()
                        .requestMatchers("/employees/all-employees", "/employees/{id}/update-password").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers("/employees/{id}/admin-code").hasAuthority("ROLE_USER")
                        .requestMatchers("/auth/registration", "/employees/{id}", "/employees/{id}/edit-employee",
                                "/tasks", "/tasks/{id}/delete-task", "/tasks/search-tasks", "/tasks/search", "/tasks/{id}/assign", "/tasks/{id}/release", "/tasks/add-task",
                                "/tasks/{id}/edit-task").hasAuthority("ROLE_ADMIN")
                        .anyRequest().hasAnyAuthority("ROLE_USER", "ROLE_ADMIN"))

                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/process_login")
                        .successHandler((request, response, authentication) -> {
                            EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();
                            Integer employeeId = employeeDetails.getId();
                            response.sendRedirect("/employees/" + employeeId + "/profile");
                        })
                        .failureUrl("/auth/login?error"))
                .userDetailsService(userDetailsService)

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("MY_SESSION_ID", "JSESSIONID"))

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)
                        .expiredUrl("/auth/login?expired"))
                .build();
    }

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
            sessionCookieConfig.setHttpOnly(true);
            sessionCookieConfig.setSecure(false);
            sessionCookieConfig.setMaxAge(3600);
            sessionCookieConfig.setName("MY_SESSION_ID");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

