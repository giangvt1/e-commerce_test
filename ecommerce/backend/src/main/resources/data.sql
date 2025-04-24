-- Insert roles if they don't exist
IF NOT EXISTS (SELECT * FROM roles WHERE name = 'ROLE_USER')
BEGIN
    INSERT INTO roles (name) VALUES ('ROLE_USER')
END

IF NOT EXISTS (SELECT * FROM roles WHERE name = 'ROLE_ADMIN')
BEGIN
    INSERT INTO roles (name) VALUES ('ROLE_ADMIN')
END
