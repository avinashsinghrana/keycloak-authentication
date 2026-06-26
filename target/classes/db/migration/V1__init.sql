-- Flyway Migration script for V1
CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    keycloak_id VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    mobile VARCHAR(20) UNIQUE,
    status VARCHAR(50) NOT NULL, -- PENDING_VERIFICATION, ACTIVE, INACTIVE, SUSPENDED, DELETED
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE registered_device (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_user(id),
    device_id VARCHAR(255) NOT NULL,
    device_name VARCHAR(255),
    status VARCHAR(50) NOT NULL, -- PENDING_VERIFICATION, ACTIVE, BLOCKED, LOST, DELETED
    is_trusted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE(user_id, device_id)
);

CREATE TABLE api_master (
    id UUID PRIMARY KEY,
    method VARCHAR(10) NOT NULL,
    path VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    UNIQUE(method, path)
);

CREATE TABLE role_api_mapping (
    id UUID PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL,
    api_id UUID NOT NULL REFERENCES api_master(id),
    created_at TIMESTAMP NOT NULL,
    UNIQUE(role_name, api_id)
);

CREATE TABLE user_session (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_user(id),
    device_id UUID REFERENCES registered_device(id),
    refresh_token_hash VARCHAR(512),
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE audit_log (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES app_user(id),
    action VARCHAR(255) NOT NULL,
    details TEXT,
    ip_address VARCHAR(50),
    created_at TIMESTAMP NOT NULL
);
