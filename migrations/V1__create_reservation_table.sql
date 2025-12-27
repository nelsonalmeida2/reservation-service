CREATE TABLE IF NOT EXISTS reservation (
                                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    party_size INT NOT NULL CHECK (party_size > 0),
    is_confirmed BOOLEAN DEFAULT FALSE,
    is_cancelled BOOLEAN DEFAULT FALSE,
    is_pending BOOLEAN DEFAULT TRUE,
    scheduled_at TIMESTAMPTZ NOT NULL,
    restaurant_id UUID NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_reservation_restaurant_id ON reservation(restaurant_id);
CREATE INDEX IF NOT EXISTS idx_reservation_customer_email ON reservation(customer_email);
CREATE INDEX IF NOT EXISTS idx_reservation_scheduled_at ON reservation(scheduled_at);
