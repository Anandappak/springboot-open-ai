# Village Temple Management System

## Overview
This project is a Spring Boot temple management system for a village temple office. It allows the administrator to manage members, donations, and events through a dashboard UI and REST APIs. The app stores data in an in-memory H2 database, making it easy to run locally for demos, testing, and small-scale administration.

The system supports:
- managing temple members and committee roles,
- tracking donations and their purpose,
- planning temple events and poojas,
- monitoring a live dashboard summary,
- admin login and password management from the UI.

## Features
- Admin login with DB-backed credentials
- Password change page in the dashboard UI
- Add, update, and delete temple members
- Add, update, and delete donations
- Add, update, and delete events
- Dashboard summary for total members, donations, events, and donation amount
- H2 in-memory database for local development and testing
- Spring Security with HTTP Basic authentication for protected APIs

## Tech Stack
- Java 17
- Spring Boot 3.3.4
- Maven
- Spring Web
- Spring Data JPA
- Spring Security
- H2 Database
- Spring Actuator
- HTML/CSS/JavaScript dashboard frontend
- JUnit 5 + Spring Test

## Project Structure
```text
Springboot-open-ai-search/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/opensearchassistant/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── static/
│   │           ├── index.html
│   │           ├── styles.css
│   │           └── app.js
│   └── test/
│       └── java/
│           └── com/example/opensearchassistant/
├── target/
└── .gitignore
```

## Prerequisites
Before running the project, make sure you have:
- Java 17 or newer
- Maven 3.8+
- A browser to access the dashboard and H2 console

## Configuration
The main configuration is in [src/main/resources/application.yml](src/main/resources/application.yml).

Key configuration values:
```yaml
server:
  port: 8082

spring:
  application:
    name: village-temple-management
  datasource:
    url: jdbc:h2:mem:templedb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: false
```

H2 database console:
```text
http://localhost:8082/h2-console
```

H2 connection details:
- JDBC URL: `jdbc:h2:mem:templedb`
- Username: `sa`
- Password: blank

## Default Admin Credentials
The default admin account is seeded automatically into the database:
- Username: `admin`
- Password: `temple123`

This account is stored in the `admin_users` table and can be changed from the dashboard UI.

## Run the project
### 1. Build the project
```bash
mvn clean install
```

### 2. Start the application
```bash
mvn spring-boot:run
```

### 3. Test the app
```bash
mvn test
```

## Access the dashboard
Open the following URL in a browser:
```text
http://localhost:8082/
```

From the dashboard, you can:
- log in as the admin user,
- change the admin password,
- add members, donations, and events,
- refresh the dashboard summary.

## Admin API Endpoints

### Login
```http
POST /api/admin/login
Content-Type: application/json
```

Request body:
```json
{
  "username": "admin",
  "password": "temple123"
}
```

### Change password
```http
POST /api/admin/change-password
Content-Type: application/json
```

Request body:
```json
{
  "currentPassword": "temple123",
  "newPassword": "newTemple456"
}
```

The UI sends the current password and new password to this endpoint, then updates the saved admin credential in the database.

## Temple Management API Endpoints

### Health
```http
GET /api/temple/health
```

### Dashboard summary
```http
GET /api/temple/dashboard
```

Example response:
```json
{
  "totalMembers": 5,
  "totalDonations": 12,
  "totalEvents": 3,
  "totalDonationAmount": 12500.00
}
```

### Members
```http
GET /api/temple/members
POST /api/temple/members
POST /api/temple/members/{id}
DELETE /api/temple/members/{id}
```

Example member payload:
```json
{
  "name": "Ravi",
  "role": "Trustee",
  "phoneNumber": "0771234567",
  "address": "North Lane"
}
```

### Donations
```http
GET /api/temple/donations
POST /api/temple/donations
POST /api/temple/donations/{id}
DELETE /api/temple/donations/{id}
```

Example donation payload:
```json
{
  "donorName": "Meera",
  "amount": 2500.00,
  "purpose": "Festival",
  "donationDate": "2026-09-26"
}
```

### Events
```http
GET /api/temple/events
POST /api/temple/events
POST /api/temple/events/{id}
DELETE /api/temple/events/{id}
```

Example event payload:
```json
{
  "name": "Pooja Festival",
  "eventDate": "2026-09-29",
  "description": "Temple festival celebration",
  "status": "Planned"
}
```

## How the app works
1. The application starts with the H2 database enabled.
2. Spring Security protects temple management endpoints and allows public access only to the login endpoint and static dashboard assets.
3. The admin account is created in the database automatically on startup.
4. The dashboard UI loads summary data and management records from the backend.
5. CRUD operations are handled through JPA repositories and persisted to H2.
6. The password change form updates the stored admin credential in the database.

## Example use cases
- Village temple administration
- Donation tracking for festivals and maintenance
- Volunteer and committee member directory
- Event planning for poojas and cultural activities
- Monthly temple reporting and overview dashboard

## Notes
This project is designed as a lightweight local application for temple administration and demonstration. The H2 database keeps setup simple, and the dashboard UI allows the admin to manage operations without any external services.
