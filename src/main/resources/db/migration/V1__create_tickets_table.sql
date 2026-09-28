CREATE TABLE tickets (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    description  VARCHAR(2000),
    status       VARCHAR(20) NOT NULL,
    priority     VARCHAR(20) NOT NULL,
    assignee     VARCHAR(100),
    created_at   TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP NOT NULL,
    resolved_at  TIMESTAMP
);

CREATE INDEX idx_tickets_status ON tickets (status);
CREATE INDEX idx_tickets_assignee ON tickets (assignee);
