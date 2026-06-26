package com.asr.auth.service;

import com.asr.auth.domain.entity.AppUser;
import com.asr.auth.dto.request.RegisterRequest;
import com.asr.auth.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private DeviceService deviceService;
    @Mock
    private KeycloakAdminService keycloakAdminService;
    @Mock
    private SessionService sessionService;
    @Mock
    private AuditService auditService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private AuthService authService;

    @Test
    void testRegister_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@test.com");
        request.setPassword("password");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setDeviceId("device-123");

        AppUser user = new AppUser();
        user.setId(UUID.randomUUID());
        user.setEmail("test@test.com");

        when(keycloakAdminService.createUser(anyString(), anyString(), anyString(), anyString()))
                .thenReturn("kc-id-123");
        when(userMapper.toEntity(any(RegisterRequest.class))).thenReturn(user);
        when(userService.saveUser(any(AppUser.class))).thenReturn(user);

        // Act
        authService.register(request);

        // Assert
        verify(userService).checkDuplicates("test@test.com", null);
        verify(keycloakAdminService).createUser("test@test.com", "Test", "User", "password");
        verify(keycloakAdminService).assignRole("kc-id-123", "CUSTOMER");
        verify(deviceService).registerDevice(eq(user), eq(request));
        verify(auditService).logAction(eq(user), eq("REGISTER"), anyString(), isNull());
        verify(kafkaTemplate).send(eq("auth-events"), any());
    }
}
