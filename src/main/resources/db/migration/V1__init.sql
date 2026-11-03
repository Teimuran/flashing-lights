CREATE TABLE events (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        flat_event_id BIGINT NOT NULL,
                        flat_promocode_id BIGINT NOT NULL,
                        event_url VARCHAR(512) NOT NULL,
                        is_active BOOLEAN DEFAULT true NOT NULL,
                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE promo_claims (
                              id BIGSERIAL PRIMARY KEY,
                              event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
                              telegram_id BIGINT NOT NULL,
                              phone VARCHAR(32) NOT NULL,
                              username VARCHAR(128),
                              promo_code VARCHAR(64) NOT NULL,
                              created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

                              CONSTRAINT uq_event_telegram UNIQUE (event_id, telegram_id),
                              CONSTRAINT uq_event_phone UNIQUE (event_id, phone)
);

CREATE INDEX idx_events_active ON events(is_active);