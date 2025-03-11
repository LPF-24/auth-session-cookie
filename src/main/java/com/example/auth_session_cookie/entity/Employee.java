package com.example.auth_session_cookie.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "employee_name", nullable = false, length = 100)
    private String employeeName;

    @Column(nullable = false, length = 30)
    private String password;

    @Column(nullable = false, length = 100)
    private String position;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, length = 100)
    private String role;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> taskList;

    //ПЕРЕСМОТРЕТЬ!
    @Override
    public String toString() {
        return "Employee{" +
                "employeeName='" + employeeName + '\'' +
                ", position='" + position + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", role='" + role + '\'' +
                ", taskList=" + taskList +
                '}';
    }
}
