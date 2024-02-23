## Authentication Microservice

This microservice is dedicated to handling authentication (verifying user credentials) and authorization (granting access based on roles and permissions). It implements relationships and role-based access control (RBAC) for any applications, allowing for flexible and granular permission management. This microservice is essentially streamlined for secure authentication and authorization processes and focuess on the:

  - **Credentials:** Email and password (hashed) for authentication. Email is used a unqiue identifier for username.

  - **Roles and Permissions:** Associations with roles (and indirectly permissions) to define what the user is authorized to do within the system.

  - **Security and State Management:** Fields relevant to security, such as account active status, but not necessarily detailed profile information.

## What is stored

This microservice has its own database and a way to access user credentials and permission settings securely. This database stores user authentication information (like usernames and passwords) and authorization details (like roles and permissions) to validate and manage access tokens. This setup enables the service to authenticate users and generate tokens without retaining user session information, making it stateless.

## Models and Tables

The models and/or tables are designed to store user credentials and roles/permissions for managing access control. Here's an overview of what these might look like:

**User Table:** Stores information about the user.
  - Fields: UserID, Email, Password (hashed), IsActive, CreatedAt, UpdatedAt

**Role Table:** Defines roles or groups.
  - Fields: RoleID, RoleName, Description, CreatedAt, UpdatedAt

**Permission Table:** Defines specific actions or operations that can be performed.
  - Fields: PermissionID, PermissionName, Description, CreatedAt, UpdatedAt

**UserRole Table:** Associates users with roles.
  - Fields: UserID, RoleID

**RolePermission Table:** Associates roles with permissions, determining what actions each role can perform.
  - Fields: RoleID, PermissionID

This structure allows for a flexible and scalable authorization system. Users are assigned roles, and roles are assigned permissions. When a user attempts to perform an action, the system checks if their roles have the necessary permissions. This design supports a stateless authentication mechanism, where each request includes a token (e.g., JWT) that contains the user's identity and claims, which are verified against the stored user credentials and permissions without maintaining session state.

## Database and Schema:

This microservice uses in-memory hsqldb relational database at the moment. The schema and test sample data are in the src/**test/resources/auth/testdb** directory. The test data maps to the JPA entities with `@ManyToMany` relationships in the following way:

1. **User and Role Relationship:**

  - The `User` entity has a `Set<Role>` to represent the roles associated with a user. This is achieved through a `@ManyToMany` annotation, which specifies that multiple users can have multiple roles.

  - The `user_role` join table maps this relationship by associating `user_id` with `role_id`. For example, inserting a row in user_role with a specific `user_id` and `role_id` links a user to a role, reflecting the `@ManyToMany` relationship in the database.

2. **Role and Permission Relationship:**

The Role entity contains a `Set<Permission>` to represent the permissions associated with a role. This relationship is also marked with `@ManyToMany`, indicating that each role can have multiple permissions.
The `role_permission` join table facilitates this association by linking `role_id` with `permission_id`. Inserting a row in role_permission connects a role to a permission, mirroring the `@ManyToMany` relationship in the entities.

### How the Test Data Reflects These Relationships:

**Users to Roles:**

  - Users (e.g., 'user1', 'admin', 'superadmin') are inserted into the `users` table.
  - Roles (`ROLE_USER`, `ROLE_ADMIN`, `ROLE_SUPERADMIN`) are inserted into the `roles` table.
  - The `user_role` table entries create the actual mappings (e.g., 'user1' is linked to `ROLE_USER`, 'admin' to `ROLE_ADMIN`, and so on), effectively implementing the `@ManyToMany` relationship between User and Role.

**Roles to Permissions:**

  - Permissions (e.g., 'CREATE', 'READ', 'UPDATE', 'DELETE') are defined in the `permissions` table.
  - The `role_permission` table's entries link roles to permissions (e.g., `ROLE_ADMIN` might be linked to 'CREATE', 'READ', 'UPDATE'), representing the `@ManyToMany` relationship between Role and Permission.

## Docker Build and Run

Navigate to the project root directory and run the following commands:

```
$ docker build -f docker/Dockerfile -t auth-service .

$ docker run -d -p 8080:8080 --name auth-service auth-service

$ docker ps -a
CONTAINER ID   IMAGE                           COMMAND                  CREATED          STATUS                   PORTS                    NAMES
15068bae9235   auth-service                    "java -jar /authenti…"   10 seconds ago   Up 9 seconds             0.0.0.0:8080->8080/tcp   cool_rhodes
```

## Test Endpoints

### /login
```
curl --location 'http://localhost:8080/api/auth/login' \
--header 'Content-Type: application/json' \
--header 'Cookie: csrftoken=dKfnaHvGGbj9B1fsjNpLoRsS37QiH9fa' \
--data-raw '{
    "email": "user1@example.com",
    "password": "changeme"
}'
```

### /logout
```
curl --location --request POST 'http://localhost:8080/api/auth/logout' \
--header 'Authorization: Bearer <TOKEN FROM THE PREVIOUS COMMAND>'
```



