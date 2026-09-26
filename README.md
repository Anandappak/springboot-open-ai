# Spring Boot OpenSearch AI Assistant

## Overview
This project is a Spring Boot application that combines OpenSearch and AI to create a simple retrieval-augmented generation (RAG) assistant. The goal is to answer user questions using relevant documents retrieved from a search engine and then generate a response based on that context.

In simple terms, the app does this:
- accepts a user question,
- searches matching content in OpenSearch,
- gathers the most relevant results,
- sends the result context to an AI model,
- returns a grounded answer with source context.

This is useful for enterprise search, internal knowledge assistants, support bots, and document Q&A systems.

## What the project does
The project demonstrates a complete AI search flow:
- REST API for user queries
- OpenSearch-style document retrieval
- Context formatting for the AI layer
- OpenAI-compatible answer generation
- Fallback response when external services are not configured
- Health endpoint for monitoring

## Why this project is useful
This type of solution is helpful when users want accurate answers from internal documents and knowledge bases instead of relying only on a general model without sources.

### Benefits
- Fast semantic or keyword search through OpenSearch
- More accurate answers because the model uses retrieved context
- Easy to extend for document search, support chat, knowledge base systems, and FAQs
- Spring Boot-based REST API structure for easy deployment
- Suitable for demos, prototypes, and practical enterprise use cases

## Tech Stack
- Java 17
- Spring Boot 3.3.x
- Maven
- Spring Web
- Spring Validation
- Spring Actuator
- OpenSearch Java client / search integration
- WebClient for HTTP-based external service calls
- OpenAI-compatible API integration
- JUnit 5 + AssertJ for testing

## Architecture
The application follows a clean multi-layer pattern:

1. Controller layer
   - receives HTTP requests
   - validates the request payload
   - returns JSON responses

2. Service layer
   - orchestrates the assistant workflow
   - retrieves search documents
   - builds the prompt for the AI model

3. Search service
   - queries OpenSearch for the best matching documents
   - returns ranked results

4. AI service
   - calls the AI model using the retrieved context
   - generates the final answer

5. Configuration layer
   - reads OpenSearch and OpenAI settings from application properties

## Project Structure
```text
springboot-open-ai-search/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/opensearchassistant/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── model/
│   │   │       └── service/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/
│           └── com/example/opensearchassistant/service/
└── target/
```

## Prerequisites
Before running this project, make sure you have:
- Java 17 or newer
- Maven 3.8+
- An OpenSearch instance running locally or in a remote environment
- An OpenAI API key if you want live AI generation

## Configuration
The application configuration is stored in [src/main/resources/application.yml](src/main/resources/application.yml).

Example:
```yaml
server:
  port: 8080

spring:
  application:
    name: springboot-open-ai-search

opensearch:
  host: http://localhost:9200
  username: admin
  password: admin
  index: documents

openai:
  api-key: ${OPENAI_API_KEY:}
  model: gpt-4o-mini
  base-url: https://api.openai.com/v1
```

### How to use it
- Set the OpenSearch host and index if you are using a real OpenSearch cluster.
- Set the OpenAI API key if you want real LLM responses.
- If the API key is missing, the app uses a fallback response built from the retrieved context.

## Implementation Steps
### 1. Clone the project
```bash
git clone <repository-url>
cd springboot-open-ai-search
```

### 2. Build the project
```bash
mvn clean install
```

### 3. Run the application
```bash
mvn spring-boot:run
```

### 4. Test the app
```bash
mvn test
```

### 5. Call the API
Send a POST request to:
```http
POST /api/ask
Content-Type: application/json
```

Request body example:
```json
{
  "query": "What is OpenSearch?",
  "maxResults": 5
}
```

Example response:
```json
{
  "answer": "OpenSearch is a distributed search and analytics engine...",
  "results": [
    {
      "id": "opensearch-1",
      "title": "OpenSearch Overview",
      "content": "OpenSearch is a distributed search and analytics engine built for scale."
    }
  ]
}
```

## Health check
```http
GET /api/health
```

Expected response:
```text
OK
```

## How the app works
The workflow is:
1. User sends a search question.
2. The backend validates the input.
3. OpenSearch is queried for matching documents.
4. The top results are converted into context.
5. A prompt is created with the user question and retrieved documents.
6. The AI model answers using that context.
7. The final answer and source results are returned to the client.

## Example use cases
- Internal knowledge base assistant
- HR policy search assistant
- Product documentation help desk
- Developer support bot
- Customer support search assistant

## Future improvements
The project can be extended with:
- real document indexing into OpenSearch,
- better relevance tuning and scoring,
- embedding-based semantic search,
- user authentication and authorization,
- response caching,
- multi-model support,
- dashboard and UI frontend,
- Docker and Kubernetes deployment.

## Notes
This project is a starter implementation for an AI-powered search assistant. It is structured so it can be extended into a production-ready application with live OpenSearch indexing, enterprise search, and real AI-powered responses.
