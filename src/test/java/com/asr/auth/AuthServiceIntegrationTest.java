package com.asr.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AuthServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        // Mock kafka and redis for simple context load test
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
        registry.add("spring.data.redis.host", () -> "localhost");
        
        // Mock Keycloak url so context starts without real keycloak
        registry.add("keycloak.server-url", () -> "http://localhost:8080");
        registry.add("keycloak.realm", () -> "test-realm");
        registry.add("keycloak.client-id", () -> "test-client");
        registry.add("keycloak.client-secret", () -> "secret");
        
        // Mock security issuer uri
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> "http://localhost:8080/realms/test-realm");
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> "http://localhost:8080/realms/test-realm/protocol/openid-connect/certs");
    }

    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.boot.test.web.client.TestRestTemplate restTemplate;

    @Test
    void testSwaggerUiLoads() {
        org.springframework.http.ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);
        System.out.println("Swagger Response Status: " + response.getStatusCode());
        System.out.println("Swagger Response Body: " + response.getBody());
    }

    @Test
    void contextLoads() {
        // Simple test to ensure the application context loads successfully with DB schema created by Flyway
    }
}
