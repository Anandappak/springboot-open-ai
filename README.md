# Village Temple Management System

A Spring Boot application for managing a village temple office, including member records, donation tracking, event scheduling, dashboard reporting, and an AI-powered assistant built on top of local repository data and optional OpenSearch/OpenAI integrations.

## Overview

This project combines:
- a temple management dashboard for admin operations,
- public donation collection for temple visitors,
- backend APIs for CRUD operations,
- an AI assistant that answers questions based on temple knowledge,
- an MCP-style server that exposes temple tools and search endpoints for external integrations.

The application is intended for local/demo use, with an in-memory H2 database and a simple front-end UI.

## Features

- Admin login and password management
- Dashboard overview for members, donations, events, and totals
- Add, update, and delete temple members
- Add, update, and delete donation records
- Add, update, and delete temple events
- Public donation form for visitors
- AI assistant for natural-language temple Q&A
- MCP-compatible endpoints for search and tool queries
- OpenSearch indexing support for knowledge documents
- Optional OpenAI API integration for answer generation
- Spring Security with HTTP Basic auth for protected endpoints

## Tech Stack

- Java 17
- Spring Boot 3.3.4
- Maven
- Spring Web
- Spring Data JPA
- Spring Security
- H2 Database
- Spring Actuator
- HTML, CSS, JavaScript frontend
- OpenSearch-compatible indexing client
- OpenAI API client via WebClient
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
│   │           ├── admin.html
│   │           ├── app.js
│   │           └── styles.css
│   └── test/
│       └── java/
│           └── com/example/opensearchassistant/
├── target/
├── .gitignore
└── .mvn/
```

## Prerequisites

Before running the project, make sure you have:
- Java 17 or newer
- Maven 3.8+
- A browser for the dashboard UI
- Optional: an OpenSearch instance if you want indexing/search integration enabled
- Optional: an OpenAI API key if you want generated AI answers instead of fallback responses

## Configuration

The main settings are in `src/main/resources/application.yml`.

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
  security:
    user:
      name: admin
      password: temple123
      roles: ADMIN
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: false

management:
  endpoints:
    web:
      exposure:
        include: health,info

opensearch:
  host: http://localhost:9200
  username: admin
  password: admin
  index: temple-documents

openai:
  api-key: ${OPENAI_API_KEY:}
  model: gpt-4o-mini
  base-url: https://api.openai.com/v1
```

### Optional external dependencies

- OpenSearch is used for document indexing and search support. If it is not available, the app still works with local DB-backed knowledge generation and fallback responses.
- OpenAI is optional. When `openai.api-key` is empty, the assistant falls back to context-aware answers without calling the external API.

## Default Admin Credentials

The default admin account is seeded automatically when the app starts:
- Username: `admin`
- Password: `temple123`

You can change this password from the admin dashboard UI.

## Run the Project

### 1. Build the project

```bash
mvn clean install
```

### 2. Start the application

```bash
mvn spring-boot:run
```

### 3. Run tests

```bash
mvn test
```

## Access the App

Open the app in a browser:

```text
http://localhost:8082/
```

The UI includes:
- a public donation page,
- an admin dashboard,
- a temple assistant panel for search/Q&A.

Also available:

```text
http://localhost:8082/h2-console
```

H2 connection details:
- JDBC URL: `jdbc:h2:mem:templedb`
- Username: `sa`
- Password: blank

## Admin Dashboard

Log in with the default admin account:
- Username: `admin`
- Password: `temple123`

From the dashboard you can:
- view a summary of temple metrics,
- create and update members,
- track donations,
- schedule or update events,
- change the admin password.

## API Endpoints

### Authentication

#### Admin login

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

#### Change admin password

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

### Temple management

```http
GET /api/temple/dashboard
GET /api/temple/members
POST /api/temple/members
POST /api/temple/members/{id}
DELETE /api/temple/members/{id}

GET /api/temple/donations
POST /api/temple/donations
POST /api/temple/donations/{id}
DELETE /api/temple/donations/{id}

GET /api/temple/events
POST /api/temple/events
POST /api/temple/events/{id}
DELETE /api/temple/events/{id}
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

Example donation payload:

```json
{
  "donorName": "Meera",
  "amount": 2500.00,
  "purpose": "Festival",
  "donationDate": "2026-09-26",
  "paymentMethod": "UPI",
  "upiId": "templedonation@upi"
}
```

Example event payload:

```json
{
  "name": "Navaratri Festival",
  "eventDate": "2026-09-29",
  "description": "Temple festival celebration",
  "status": "Planned"
}
```

### Public donation API

```http
POST /api/public/donations
```

This endpoint is intended for non-admin donors who want to submit temple donations through the public form.

### Assistant API

```http
POST /api/ask
GET /api/health
```

Example request:

```json
{
  "query": "Who is the trustee for the temple?",
  "maxResults": 5
}
```

Example response:

```json
{
  "answer": "The temple committee includes Ravi as Trustee...",
  "results": [
    {
      "id": "member:1",
      "title": "Ravi - Trustee",
      "content": "Temple member Ravi holds the role of Trustee..."
    }
  ]
}
```

### MCP server endpoints

This project exposes a lightweight Model Context Protocol style API for tool-driven temple integrations.

```http
GET /api/mcp/health
GET /api/mcp/tools
POST /api/mcp/search
GET /api/mcp/dashboard
POST /api/mcp/index
```

#### Example tool list

```json
[
  {
    "name": "searchTempleData",
    "description": "Search temple members, donations, and events using natural language",
    "inputSchema": {
      "type": "object",
      "properties": {
        "query": { "type": "string" },
        "entityType": { "type": "string", "enum": ["all", "member", "donation", "event"] },
        "filters": { "type": "object" }
      },
      "required": ["query"]
    }
  }
]
```

#### Example MCP search request

```json
{
  "query": "Kavya pooja schedule",
  "entityType": "all",
  "filters": {}
}
```

## How It Works

1. The system uses H2 in-memory storage for local temple records.
2. Spring Security protects admin and management endpoints.
3. The UI reads dashboard metrics and records from the backend.
4. The assistant queries local temple data and optionally OpenSearch results.
5. OpenAI is used when configured to generate a final answer from retrieved context.
6. The MCP endpoints expose the same temple knowledge and tools to external clients.

## Example Use Cases

- Temple committee administration
- Donor and contribution tracking
- Festival and pooja event planning
- Community member directory and contact management
- AI-powered search over temple information
- External tool integration via MCP-compatible endpoints

## Notes

This project is designed as a lightweight local application for demonstration and small-scale use. It is intentionally simple to run, secure enough for local use, and easy to extend with real OpenSearch or OpenAI services in a production environment.

## Troubleshooting

### App won’t start

- Ensure Java 17+ is installed.
- Ensure Maven dependencies are downloaded with `mvn clean install`.
- Check the port `8082` is not already in use.

### OpenSearch not available

- The app can still run without a live OpenSearch node.
- Knowledge indexing actions will simply be skipped or fall back to local DB-based behavior.

### OpenAI key missing

- The assistant will still answer using context and fallback logic.
- The app does not fail startup when the API key is empty.

## License

This project is for educational and local development use unless otherwise specified by the repository owner.
