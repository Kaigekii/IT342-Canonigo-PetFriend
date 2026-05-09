# Auth Feature - Frontend Documentation

## Overview
The Auth feature handles user authentication, registration, and user profile management. It provides hooks and API clients for integration with the backend auth service.

## Directory Structure
```
features/auth/
├── api.js           # API client functions
├── constants.js     # Auth-specific constants
├── hooks/
│   ├── useAuth.js   # Auth state management hook
│   └── useLogin.js  # (Optional) Login form-specific logic
├── components/
│   ├── LoginForm.js
│   ├── RegisterForm.js
│   ├── RegisterOwnerForm.js
│   ├── RegisterSitterForm.js
│   └── README.md
└── README.md
```

## API Client

### Import
```javascript
import { authApi } from "@/features/auth/api";
```

### Methods

#### `authApi.login(email, password)`
Authenticate user with email and password.
```javascript
const response = await authApi.login("user@example.com", "Password@123");
// Returns: { token: "jwt...", user: {...} }
localStorage.setItem("token", response.token);
```

#### `authApi.register(formData)`
Register new user.
```javascript
const formData = {
  email: "user@example.com",
  password: "Password@123",
  fullName: "John Doe",
  role: "PET_OWNER"
};
const response = await authApi.register(formData);
// Returns: { token: "jwt...", user: {...} }
```

#### `authApi.getCurrentUser()`
Get authenticated user information.
```javascript
const user = await authApi.getCurrentUser();
// Returns: { userId, email, fullName, role, isVerified, createdAt }
```

#### `authApi.updateProfile(profileData)`
Update user profile.
```javascript
const updated = await authApi.updateProfile({
  fullName: "Jane Doe",
  phone: "+63 9XX..."
});
```

## Hooks

### useAuth Hook
Manages authentication state and provides login/logout functionality.

#### Import
```javascript
import { useAuth } from "@/shared/hooks/useAuth";
```

#### Usage
```javascript
"use client";

export default function Dashboard() {
  const { user, loading, isAuthenticated, logout } = useAuth();

  if (loading) return <div>Loading...</div>;

  if (!isAuthenticated) {
    return <div>Please log in</div>;
  }

  return (
    <div>
      <h1>Welcome, {user?.fullName}</h1>
      <button onClick={logout}>Logout</button>
    </div>
  );
}
```

#### Properties
- `user` - Current user object
- `loading` - Boolean indicating if auth is being fetched
- `error` - Error message if any
- `isAuthenticated` - Boolean indicating if user is logged in
- `login(email, password)` - Login function
- `register(formData)` - Register function
- `logout()` - Logout function
- `refetch()` - Manually refresh user info

## Example Components

### LoginForm Component
```javascript
"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/shared/hooks/useAuth";
import { validateEmail } from "@/shared/constants/validation";
import { ErrorBanner } from "@/shared/components/Banners";

export default function LoginForm() {
  const router = useRouter();
  const { login, loading, error } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [formError, setFormError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate
    const emailError = validateEmail(email);
    if (emailError) {
      setFormError(emailError);
      return;
    }

    try {
      await login(email, password);
      router.push("/dashboard");
    } catch (err) {
      setFormError(err.message);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {(formError || error) && <ErrorBanner message={formError || error} />}
      
      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Email"
        required
      />
      
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Password"
        required
      />
      
      <button type="submit" disabled={loading}>
        {loading ? "Logging in..." : "Login"}
      </button>
    </form>
  );
}
```

## Page Integration

### Update `/app/login/page.js`
```javascript
import LoginForm from "@/features/auth/components/LoginForm";

export default function LoginPage() {
  return (
    <div>
      <h1>Login to PetFriend</h1>
      <LoginForm />
    </div>
  );
}
```

## Constants

### Auth-specific constants (if needed)
```javascript
// features/auth/constants.js
export const AUTH_ERRORS = {
  INVALID_CREDENTIALS: "Invalid email or password",
  USER_EXISTS: "User with this email already exists",
  WEAK_PASSWORD: "Password is too weak",
};
```

## Error Handling

The API client automatically redirects to `/login` on 401 errors.

```javascript
try {
  const user = await authApi.getCurrentUser();
} catch (err) {
  // If 401: automatically redirected to /login
  // Otherwise: handle error
  console.error("Error:", err.message);
}
```

## Best Practices

1. **Token Storage**: Always use `localStorage.setItem("token", response.token)`
2. **User Validation**: Always check `isAuthenticated` before rendering protected content
3. **Error Messages**: Display user-friendly error messages from `useAuth().error`
4. **Loading States**: Show loading indicator while `useAuth().loading` is true
5. **Role-based Routes**: Check `user.role` to determine which routes to show

## Related Features
- [Pets Feature](../pets/README.md)
- [Bookings Feature](../booking/README.md)
- [Admin Feature](../admin/README.md)

---

**Created**: May 9, 2026  
**Version**: 1.0  
**Status**: Complete
