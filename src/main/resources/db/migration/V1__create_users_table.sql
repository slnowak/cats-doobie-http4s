CREATE TABLE users(
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    firstname VARCHAR NOT NULL,
    lastname VARCHAR NOT NULL
);
