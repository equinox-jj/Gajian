CREATE TABLE app_user
(
    id        UUID         NOT NULL PRIMARY KEY,
    email     VARCHAR(255) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    role      VARCHAR(20)  NOT NULL,
    is_active BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE refresh_token
(
    id                   UUID                     NOT NULL PRIMARY KEY,
    user_id              UUID                     NOT NULL REFERENCES app_user (id),
    token_hash           VARCHAR(64)              NOT NULL UNIQUE,
    expires_at           timestamp with time zone NOT NULL,
    revoked_at           timestamp with time zone,
    replaced_by_token_id UUID REFERENCES refresh_token (id)
);

CREATE INDEX idx_refresh_token_user_id ON refresh_token (user_id);
