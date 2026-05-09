# Auth Feature Slice

## Overview
Handles user authentication including registration and login for all user roles (PET_OWNER, PET_SITTER, ADMIN).

## Structure
```
features/auth/
├── AuthController.java    - REST endpoints
├── AuthDtos.java          - Request/Response DTOs
└── (AuthService.java)     - Service layer (if needed)
```

## API Endpoints

### POST /api/auth/register
Register a new user with one of three roles:
- **PET_OWNER**: Instant verification (no admin approval needed)
- **PET_SITTER**: Unverified (requires admin approval)
- **ADMIN**: Instant verification

**Request:**
```json
{
  "firstName": "string",
  "lastName": "string",
  "email": "user@example.com",
  "password": "SecurePass123!",
  "phoneNumber": "string (optional)",
  "address": "string (optional)",
  "role": "PET_OWNER|PET_SITTER|ADMIN"
}
```

**Response:**
```json
{
  "token": "jwt_token",
  "userId": "uuid",
  "firstName": "string",
  "lastName": "string",
  "email": "user@example.com",
  "phoneNumber": "string",
  "address": "string",
  "role": "PET_OWNER|PET_SITTER|ADMIN",
  "isVerified": true|false|null
}
```

### POST /api/auth/login
Authenticate user and receive JWT token.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

**Response:**
```json
{
  "token": "jwt_token",
  "userId": "uuid",
  "firstName": "string",
  "lastName": "string",
  "email": "user@example.com",
  "phoneNumber": "string",
  "address": "string",
  "role": "PET_OWNER|PET_SITTER|ADMIN",
  "isVerified": true|false|null
}
```

## Password Requirements
- Minimum 8 characters
- At least 1 uppercase letter
- At least 1 number
- At least 1 special character (!@#$%^&*(),.?":{}|<>)

## Validation Rules
- Email must be unique
- Email must be in valid format
- Role must be one of: PET_OWNER, PET_SITTER, ADMIN

## Dependencies
- `UserRepository`: Database access for users
- `PasswordEncoder`: Spring Security password encoding
- `AuthenticationManager`: Spring Security authentication
- `TokenProvider`: JWT token generation

## Notes
- JWT tokens are stored client-side and sent in Authorization header
- Sitters must be approved by admin before they can fully use the system
- Last login timestamp is updated on successful login
