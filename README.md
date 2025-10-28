# 📝 Auth Session Cookie

This is a Java-based task manager application with role-based authorization, Cookie and Sessions-based session handling.

The application follows a clean layered architecture with a clear separation of concerns: controllers handle HTTP requests, services encapsulate business logic, and repositories manage access to data.

## 🚀 Technologies Used

- Java 17
- Spring Boot 3
- Spring Security (Session + Cookie-based auth)
- Spring Data JPA + Hibernate
- Thymeleaf + HTML
- PostgreSQL
- Docker & Docker Compose

## 📦 Features

- 👥 Registration & login (Cookie + Session)
- 🪪 Account creation, update, deletion
- 📌 Display information about employees and tasks
- 📝 Task management (admin)
- 👤 Role-based access (User/Admin)
- 🧩 Input validation with custom annotations

## 🐳 Run with Docker

```bash
docker-compose up --build
```

- Frontend: `http://localhost:8080`

## 👥 Sample Accounts

| Role  | Username           | Password   |
|-------|--------------------|------------|
| Admin | `admin@example.com` | `Test234!` |
| User  | `user@example.com` | `user123!` |

In order for a user with the **ROLE_USER** role to receive **ROLE_ADMIN**, they need to click the **"Become an admin"** button, enter the code **160000** and click the **"Send code!"** button.

## 🔐 Cookie + Session Authentication

1. `POST /auth/login` returns personal account page at /employees/{id}/profile
2. `POST /auth/login` invalidates the HTTP session and deletes cookies

## 📌 Project Status

✅ Core functionality is complete.  
🛠 JavaDoc and integration testing to be added in future iterations.

## 👤 Author

**LPF-24** — aspiring Java backend developer building real-world projects with Spring, REST APIs, Thymeleaf and Docker.

GitHub: [@LPF-24](https://github.com/LPF-24)

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.