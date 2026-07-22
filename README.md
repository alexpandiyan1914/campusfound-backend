# CampusFound

CampusFound is a Lost & Found Management System for educational institutions. It provides a secure platform for students to report lost and found items, search existing records, and submit ownership claims. Administrators review claims before items are returned to their owners.

---

## Features

- User authentication using JWT
- Student and administrator roles
- Report lost items
- Report found items
- Search and filter items
- Submit ownership claims
- Claim approval workflow
- PostgreSQL database hosted on Supabase

---

## Technology Stack

### Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Maven
- Lombok

### Mobile

- React Native
- Expo

---

## Architecture

```
React Native App
        │
        ▼
Spring Boot REST API
        │
        ▼
Spring Security (JWT)
        │
        ▼
PostgreSQL (Supabase)
```

---

## Repository Structure

```
campusfound-backend/
├── docs/
├── src/
│   ├── main/
│   └── test/
├── pom.xml
└── README.md
```

---

## Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL database (Supabase)

---

## Configuration

Create a `.env` file in the project root.

```env
SUPABASE_DB_URL=
SUPABASE_DB_USERNAME=
SUPABASE_DB_PASSWORD=
JWT_SECRET=
```

---

## Running the Application

Clone the repository.

```bash
git clone https://github.com/alexpandiyan1914/campusfound-backend.git
cd campusfound-backend
```

Build the project.

```bash
./mvnw clean install
```

Run the application.

```bash
./mvnw spring-boot:run
```

The server will be available at:

```
http://localhost:8080
```

---

## Documentation

The `docs` directory contains project documentation, including:

- Product Requirements
- System Architecture
- Database Design
- API Specification
- Development Roadmap


## Author
Alexpandiyan A