-- Create role_requests table for SQL Server
CREATE TABLE role_requests (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    requested_role NVARCHAR(50) NOT NULL,
    reason NTEXT NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reviewed_by BIGINT NULL,
    admin_comments NTEXT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    reviewed_at DATETIME2 NULL,
    
    -- Foreign key constraints
    CONSTRAINT FK_role_requests_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT FK_role_requests_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id),
    
    -- Check constraints
    CONSTRAINT CHK_role_requests_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    CONSTRAINT CHK_role_requests_role CHECK (requested_role IN ('Manager', 'Director', 'CEO'))
);

-- Create indexes for better performance
CREATE INDEX IDX_role_requests_user_id ON role_requests (user_id);
CREATE INDEX IDX_role_requests_status ON role_requests (status);
CREATE INDEX IDX_role_requests_created_at ON role_requests (created_at);

-- Add trigger to update updated_at timestamp
CREATE TRIGGER TR_role_requests_updated_at
ON role_requests
FOR UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE role_requests 
    SET updated_at = GETDATE() 
    WHERE id IN (SELECT id FROM inserted);
END; 