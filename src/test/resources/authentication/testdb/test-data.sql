-- Users
INSERT INTO users (email, password, is_active, created_at, updated_at)
VALUES 
('user1@example.com', '{bcrypt}$2a$10$EIoM3ay2dbNIW47wz0OSPuCWB0iGTxf4WCFbcGGhyar7sQeyOBOHe', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('userAdmin@example.com', '{bcrypt}$2a$10$EIoM3ay2dbNIW47wz0OSPuCWB0iGTxf4WCFbcGGhyar7sQeyOBOHe', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin@example.com', '{bcrypt}$2a$10$EIoM3ay2dbNIW47wz0OSPuCWB0iGTxf4WCFbcGGhyar7sQeyOBOHe', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('superadmin@example.com', '{bcrypt}$2a$10$EIoM3ay2dbNIW47wz0OSPuCWB0iGTxf4WCFbcGGhyar7sQeyOBOHe', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Roles
INSERT INTO roles (role_name, description, created_at, updated_at)
VALUES 
('ROLE_USER', 'A standard user role', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ROLE_ADMIN', 'An admin role with extended privileges', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ROLE_SUPERADMIN', 'A superadmin role with all privileges', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permissions (Example permissions, adjust as needed)
INSERT INTO permissions (permission_name, description, created_at, updated_at)
VALUES 
('CREATE', 'Create permission', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('READ', 'Read permission', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('UPDATE', 'Update permission', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DELETE', 'Delete permission', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- User Roles
INSERT INTO user_role (user_id, role_id)
VALUES 
((SELECT user_id FROM users WHERE email = 'user1@example.com'), (SELECT role_id FROM roles WHERE role_name = 'ROLE_USER')),
((SELECT user_id FROM users WHERE email = 'userAdmin@example.com'), (SELECT role_id FROM roles WHERE role_name = 'ROLE_USER')),
((SELECT user_id FROM users WHERE email = 'userAdmin@example.com'), (SELECT role_id FROM roles WHERE role_name = 'ROLE_ADMIN')),
((SELECT user_id FROM users WHERE email = 'admin@example.com'), (SELECT role_id FROM roles WHERE role_name = 'ROLE_ADMIN')),
((SELECT user_id FROM users WHERE email = 'superadmin@example.com'), (SELECT role_id FROM roles WHERE role_name = 'ROLE_SUPERADMIN'));

-- Role Permissions (Example mapping, adjust as needed)
INSERT INTO role_permission (role_id, permission_id)
VALUES 
((SELECT role_id FROM roles WHERE role_name = 'ROLE_USER'), (SELECT permission_id FROM permissions WHERE permission_name = 'READ')),
((SELECT role_id FROM roles WHERE role_name = 'ROLE_ADMIN'), (SELECT permission_id FROM permissions WHERE permission_name = 'CREATE')),
((SELECT role_id FROM roles WHERE role_name = 'ROLE_ADMIN'), (SELECT permission_id FROM permissions WHERE permission_name = 'READ')),
((SELECT role_id FROM roles WHERE role_name = 'ROLE_ADMIN'), (SELECT permission_id FROM permissions WHERE permission_name = 'UPDATE')),
((SELECT role_id FROM roles WHERE role_name = 'ROLE_SUPERADMIN'), (SELECT permission_id FROM permissions WHERE permission_name = 'CREATE')),
((SELECT role_id FROM roles WHERE role_name = 'ROLE_SUPERADMIN'), (SELECT permission_id FROM permissions WHERE permission_name = 'READ')),
((SELECT role_id FROM roles WHERE role_name = 'ROLE_SUPERADMIN'), (SELECT permission_id FROM permissions WHERE permission_name = 'UPDATE')),
((SELECT role_id FROM roles WHERE role_name = 'ROLE_SUPERADMIN'), (SELECT permission_id FROM permissions WHERE permission_name = 'DELETE'));

