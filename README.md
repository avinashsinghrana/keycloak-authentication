# Auth Service

Authentication and Authorization Service built with Spring Boot, Keycloak, PostgreSQL, Redis, and Kafka.

## Prerequisites
- Java 21
- Maven
- Docker and Docker Compose

## Setup Instructions

1. Start Infrastructure:
   ```bash
   docker-compose up -d
   ```
   This will start PostgreSQL, Keycloak, Redis, Zookeeper, and Kafka.

2. Build the Application:
   ```bash
   mvn clean install -DskipTests
   ```

3. Run the Application:
   ```bash
   mvn spring-boot:run
   ```

## Keycloak Setup

1. Login to Keycloak Admin Console (`http://localhost:8080`) using `admin` / `admin`.
2. Create a Realm `microservice-realm`.
3. Create a Client `auth-service-client` with Access Type `confidential` (Client Authentication enabled).
4. Save the Client Secret and update `application.yml` -> `spring.security.oauth2.client.registration.keycloak.client-secret`.
5. Enable Service Accounts Roles in Client Settings.
6. Assign Realm Management -> `manage-users`, `view-users` roles to the Service Account.

## API Documentation

Once the app is running, access Swagger UI at:
`http://localhost:8081/swagger-ui.html`

