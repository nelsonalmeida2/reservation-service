CREATE TABLE IF NOT EXISTS reservation (
                                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    party_size INT NOT NULL CHECK (party_size > 0),
    status VARCHAR(16) NOT NULL,
    scheduled_at TIMESTAMPTZ NOT NULL,
    restaurant_id UUID NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
    );