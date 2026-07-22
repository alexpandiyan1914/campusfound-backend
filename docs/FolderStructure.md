# CampusFound Backend Folder Structure

**Project:** CampusFound Backend

---

# Root Structure

```
campusfound-backend
│
├── docs/
├── src/
├── target/
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
├── README.md
```

---

# Source Structure

```
src
└── main
    ├── java
    │   └── com
    │       └── campusfound
    │
    │           ├── auth/
    │           │
    │           ├── category/
    │           │
    │           ├── claim/
    │           │
    │           ├── common/
    │           │
    │           ├── config/
    │           │
    │           ├── exception/
    │           │
    │           ├── item/
    │           │
    │           ├── notification/
    │           │
    │           ├── security/
    │           │
    │           ├── user/
    │           │
    │           └── CampusFoundApplication.java
    │
    └── resources
        ├── static/
        ├── templates/
        └── application.properties
```

---

# Package Breakdown

## auth/

Responsible for authentication.

Contains:

```
controller/
service/
repository/
dto/
```

Handles

- Login
- Register
- JWT Authentication

---

## user/

Responsible for user management.

Contains

```
controller/
service/
repository/
entity/
dto/
```

Handles

- Student
- Admin
- Profile

---

## item/

Responsible for lost & found items.

Contains

```
controller/
service/
repository/
entity/
dto/
```

Handles

- Report Lost Item
- Report Found Item
- Search Items
- Update Status

---

## claim/

Responsible for ownership claims.

Contains

```
controller/
service/
repository/
entity/
dto/
```

Handles

- Claim Requests
- Approve
- Reject

---

## category/

Responsible for categories.

Examples

- Wallet
- Laptop
- Bottle
- Keys

Contains

```
controller/
service/
repository/
entity/
```

---

## notification/

Responsible for notifications.

Contains

```
controller/
service/
repository/
entity/
```

---

## security/

Spring Security configuration.

Contains

```
JwtFilter
JwtService
SecurityConfig
UserDetailsServiceImpl
```

---

## config/

Application configuration.

Examples

```
CorsConfig
SwaggerConfig
OpenApiConfig
```

---

## common/

Shared components.

Examples

```
ApiResponse
Constants
Utility Classes
BaseEntity
```

---

## exception/

Global exception handling.

Contains

```
GlobalExceptionHandler
ResourceNotFoundException
BadRequestException
UnauthorizedException
```