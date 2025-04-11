# SaaS CRM - Enterprise Customer Relationship Management System

A modern, cloud-based CRM system designed for businesses of all sizes. 

## Features

- User management with role-based access control
- Customer management
- Deal tracking & pipeline management
- Task management
- Reporting & analytics
- Email integration
- JWT-based authentication

## API Documentation

The API documentation is available via Swagger UI when the application is running:

```
http://localhost:8080/swagger-ui.html
```

### Key API Endpoints

#### Authentication

| Method | Endpoint                      | Description                                   |
|--------|-------------------------------|-----------------------------------------------|
| POST   | /api/auth/login               | Authenticate user and get JWT token           |
| POST   | /api/auth/register            | Register a new user (admin only)              |
| POST   | /api/auth/refresh-token       | Refresh JWT token                             |
| POST   | /api/auth/logout              | Logout and invalidate token                   |
| POST   | /api/auth/password/change     | Change password                               |
| POST   | /api/auth/password/reset-request | Request password reset                     |
| POST   | /api/auth/password/reset      | Reset password with token                     |
| GET    | /api/auth/user-info           | Get current user info                         |

#### User Management

| Method | Endpoint                       | Description                                   |
|--------|--------------------------------|-----------------------------------------------|
| GET    | /api/users                     | Get all users (with pagination)               |
| POST   | /api/users                     | Create new user (admin only)                  |
| GET    | /api/users/{id}                | Get user by ID                                |
| PUT    | /api/users/{id}                | Update user                                   |
| DELETE | /api/users/{id}                | Delete user (admin only)                      |
| GET    | /api/users/search              | Search users                                  |
| GET    | /api/users/role/{roleName}     | Get users by role                             |
| POST   | /api/users/{id}/roles/{roleId} | Assign role to user                           |
| DELETE | /api/users/{id}/roles/{roleId} | Remove role from user                         |
| GET    | /api/users/{id}/tasks          | Get tasks assigned to user                    |
| GET    | /api/users/{id}/customers      | Get customers assigned to user                |
| GET    | /api/users/{id}/deals          | Get deals assigned to user                    |

#### Customer Management

| Method | Endpoint                       | Description                                   |
|--------|--------------------------------|-----------------------------------------------|
| GET    | /api/customers                 | Get all customers (with pagination)           |
| POST   | /api/customers                 | Create new customer                           |
| GET    | /api/customers/{id}            | Get customer by ID                            |
| PUT    | /api/customers/{id}            | Update customer                               |
| DELETE | /api/customers/{id}            | Delete customer                               |
| GET    | /api/customers/search          | Search customers                              |
| GET    | /api/customers/status/{status} | Get customers by status                       |
| GET    | /api/customers/assigned/{userId} | Get customers assigned to user               |
| PUT    | /api/customers/{id}/assign/{userId} | Assign customer to user                    |
| PUT    | /api/customers/{id}/status     | Update customer status                        |
| GET    | /api/customers/{id}/interactions | Get customer interactions                     |
| GET    | /api/customers/{id}/deals      | Get customer deals                            |
| GET    | /api/customers/{id}/tasks      | Get customer tasks                            |
| GET    | /api/customers/{id}/emails     | Get customer emails                           |
| GET    | /api/customers/recent          | Get recently added customers                  |
| GET    | /api/customers/stats           | Get customer statistics                       |
| GET    | /api/customers/{id}/assigned-users | Get users assigned to customer               |
| DELETE | /api/customers/{id}/assign/{userId} | Remove user assignment from customer         |

## Getting Started

### Prerequisites

- Java 17+
- Maven
- MySQL 8.0+
- Redis (for token blacklisting and caching)

### Configuration

1. Configure application properties in `src/main/resources/application.yml`
2. Set up database connection details
3. Configure JWT secret and expiration times

### Running the Application

```bash
mvn spring-boot:run
```

Or run with Docker Compose:

```bash
docker-compose up
```

## Security

- JWT-based authentication
- Role-based access control
- Password encryption using BCrypt
- Token blacklisting for logout
- CSRF protection
- XSS protection

## Technology Stack

- Java 17
- Spring Boot 3.4
- Spring Security
- Spring Data JPA
- MySQL
- Redis
- JWT
- Maven
- Lombok
- MapStruct
- OpenAPI/Swagger 