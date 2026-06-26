package com.asr.auth.service;

import com.asr.auth.dto.response.AuthResponse;
import com.asr.auth.exception.BusinessException;
import com.asr.auth.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminService {

    private final Keycloak keycloak;
    
    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public String createUser(String email, String firstName, String lastName, String password) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        user.setCredentials(Collections.singletonList(credential));

        UsersResource usersResource = keycloak.realm(realm).users();
        try (Response response = usersResource.create(user)) {
            if (response.getStatus() == 201) {
                String path = response.getLocation().getPath();
                String userId = path.substring(path.lastIndexOf('/') + 1);
                log.info("Created Keycloak user with id: {}", userId);
                return userId;
            } else if (response.getStatus() == 409) {
                throw new BusinessException("User already exists in Keycloak");
            } else {
                log.error("Failed to create keycloak user, status: {}", response.getStatus());
                throw new BusinessException("Failed to create user in Keycloak");
            }
        }
    }

    public void assignRole(String userId, String roleName) {
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();
        
        RoleRepresentation realmRole = realmResource.roles().get(roleName).toRepresentation();
        usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(realmRole));
        log.info("Assigned role {} to user {}", roleName, userId);
    }

    public AuthResponse authenticateUser(String username, String password) {
        String tokenUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("grant_type", "password");
        map.add("username", username);
        map.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            Map<String, Object> body = response.getBody();
            if (body != null) {
                return AuthResponse.builder()
                        .accessToken((String) body.get("access_token"))
                        .refreshToken((String) body.get("refresh_token"))
                        .expiresIn((Integer) body.get("expires_in"))
                        .refreshExpiresIn((Integer) body.get("refresh_expires_in"))
                        .tokenType((String) body.get("token_type"))
                        .build();
            }
            throw new UnauthorizedException("Invalid credentials");
        } catch (HttpClientErrorException e) {
            log.warn("Authentication failed for user {}: {}", username, e.getMessage());
            throw new UnauthorizedException("Invalid credentials");
        }
    }

    public void logoutUser(String userId) {
        keycloak.realm(realm).users().get(userId).logout();
        log.info("Logged out user {} from Keycloak", userId);
    }
}
