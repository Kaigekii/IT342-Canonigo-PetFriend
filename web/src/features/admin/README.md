# Admin Feature - Frontend Documentation

## Overview
The Admin feature provides administrative dashboard, sitter approvals, user management, and booking monitoring for system administrators.

## Directory Structure
```
features/admin/
├── api.js           # API client functions
├── hooks/
│   ├── useAdmin.js           # Admin dashboard and operations
│   └── useSitterApprovals.js # (Optional) Sitter approval workflow
├── components/
│   ├── AdminDashboard.js
│   ├── SitterApprovalQueue.js
│   ├── UserManagement.js
│   ├── BookingManagement.js
│   └── README.md
└── README.md
```

## API Client

### Import
```javascript
import { adminApi } from "@/features/admin/api";
```

### Methods

#### `adminApi.getDashboard()`
Get admin dashboard with statistics.
```javascript
const dashboard = await adminApi.getDashboard();
// Returns: { totalUsers, petOwners, petSitters, pendingApprovals, totalBookings, 
//            monthRevenue, platformFees, sitterPayouts, recentActivity[] }
```

#### `adminApi.getPendingSitters()`
Get queue of pending sitter approvals.
```javascript
const pending = await adminApi.getPendingSitters();
// Returns: [{ sitterId, fullName, email, bio, experience, hourlyRate, submittedAt, ... }, ...]
```

#### `adminApi.approveSitter(sitterId)`
Approve a sitter for verification.
```javascript
await adminApi.approveSitter(sitterId);
```

#### `adminApi.rejectSitter(sitterId, reason)`
Reject a sitter with optional reason.
```javascript
await adminApi.rejectSitter(sitterId, "Background check did not clear");
```

#### `adminApi.listUsers()`
Get all users in the system.
```javascript
const users = await adminApi.listUsers();
// Returns: [{ userId, fullName, email, role, verified, createdAt }, ...]
```

#### `adminApi.listBookings()`
Get all bookings in the system.
```javascript
const bookings = await adminApi.listBookings();
// Returns: [{ bookingId, ownerName, sitterName, serviceType, date, status, totalAmount, ... }, ...]
```

## Hooks

### useAdmin Hook
```javascript
import { useAdmin } from "@/features/admin/hooks/useAdmin";

const {
  dashboard,
  users,
  bookings,
  loading,
  error,
  getDashboard,
  listUsers,
  listBookings
} = useAdmin();
```

## Dashboard Data Structure
```javascript
{
  totalUsers: 156,
  petOwners: 98,
  petSitters: 57,
  pendingApprovals: 12,
  totalBookings: 412,
  monthRevenue: 5280.50,
  platformFees: 528.05,
  sitterPayouts: 4752.45,
  recentActivity: [
    {
      type: "user",
      message: "New user registration: John Doe (Pet Sitter)",
      ago: "2 hours ago"
    },
    ...
  ]
}
```

## Example Component: AdminDashboard

```javascript
"use client";

import { useEffect } from "react";
import { useAdmin } from "@/features/admin/hooks/useAdmin";
import { formatCurrency } from "@/shared/utils/formatting";
import { LoadingSpinner } from "@/shared/components/Banners";

export default function AdminDashboard() {
  const { dashboard, loading, error, getDashboard } = useAdmin();

  useEffect(() => {
    getDashboard();
  }, []);

  if (loading) return <LoadingSpinner message="Loading dashboard..." />;
  if (error) return <p>Error: {error}</p>;
  if (!dashboard) return <p>No data</p>;

  return (
    <div style={{ padding: "20px" }}>
      <h1>Admin Dashboard</h1>

      <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: "16px", marginBottom: "24px" }}>
        <div style={{ backgroundColor: "#FFD8B9", padding: "16px", borderRadius: "8px" }}>
          <h3>Total Users</h3>
          <p style={{ fontSize: "24px", margin: 0 }}>{dashboard.totalUsers}</p>
        </div>
        <div style={{ backgroundColor: "#B6E5D8", padding: "16px", borderRadius: "8px" }}>
          <h3>Pending Approvals</h3>
          <p style={{ fontSize: "24px", margin: 0 }}>{dashboard.pendingApprovals}</p>
        </div>
        <div style={{ backgroundColor: "#FFB6C1", padding: "16px", borderRadius: "8px" }}>
          <h3>Total Bookings</h3>
          <p style={{ fontSize: "24px", margin: 0 }}>{dashboard.totalBookings}</p>
        </div>
      </div>

      <div style={{ display: "grid", gridTemplateColumns: "repeat(2, 1fr)", gap: "16px", marginBottom: "24px" }}>
        <div style={{ backgroundColor: "#FFF9C4", padding: "16px", borderRadius: "8px" }}>
          <h3>Month Revenue</h3>
          <p>{formatCurrency(dashboard.monthRevenue)}</p>
          <p>Platform Fees: {formatCurrency(dashboard.platformFees)}</p>
          <p>Sitter Payouts: {formatCurrency(dashboard.sitterPayouts)}</p>
        </div>
        <div style={{ padding: "16px", borderRadius: "8px", border: "1px solid #DDD" }}>
          <h3>Recent Activity</h3>
          {dashboard.recentActivity?.map((activity, idx) => (
            <div key={idx} style={{ borderBottom: "1px solid #DDD", paddingBottom: "8px", marginBottom: "8px" }}>
              <p style={{ margin: "0 0 4px 0" }}>{activity.message}</p>
              <p style={{ margin: 0, fontSize: "12px", color: "#999" }}>{activity.ago}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
```

## Example Component: SitterApprovalQueue

```javascript
"use client";

import { useEffect, useState } from "react";
import { adminApi } from "@/features/admin/api";
import { ErrorBanner, SuccessBanner } from "@/shared/components/Banners";

export default function SitterApprovalQueue() {
  const [pending, setPending] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");

  useEffect(() => {
    loadPending();
  }, []);

  const loadPending = async () => {
    try {
      const data = await adminApi.getPendingSitters();
      setPending(data);
    } catch (err) {
      setMessage(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (sitterId) => {
    try {
      await adminApi.approveSitter(sitterId);
      setPending(pending.filter(s => s.sitterId !== sitterId));
      setMessage("Sitter approved!");
    } catch (err) {
      setMessage(`Error: ${err.message}`);
    }
  };

  const handleReject = async (sitterId) => {
    try {
      await adminApi.rejectSitter(sitterId, "Not approved");
      setPending(pending.filter(s => s.sitterId !== sitterId));
      setMessage("Sitter rejected!");
    } catch (err) {
      setMessage(`Error: ${err.message}`);
    }
  };

  if (loading) return <p>Loading...</p>;

  return (
    <div>
      <h2>Pending Sitter Approvals</h2>
      {message && <ErrorBanner message={message} onClose={() => setMessage("")} />}

      {pending.map(sitter => (
        <div key={sitter.sitterId} style={{ border: "1px solid #DDD", padding: "16px", marginBottom: "12px", borderRadius: "8px" }}>
          <h3>{sitter.fullName}</h3>
          <p>Email: {sitter.email}</p>
          <p>Experience: {sitter.experience}</p>
          <p>Rate: ₱{sitter.hourlyRate}/hour</p>
          <p>Bio: {sitter.bio}</p>
          <div style={{ display: "flex", gap: "8px" }}>
            <button
              onClick={() => handleApprove(sitter.sitterId)}
              style={{ backgroundColor: "#B6E5D8", padding: "8px 16px", border: "none", borderRadius: "4px", cursor: "pointer" }}
            >
              Approve
            </button>
            <button
              onClick={() => handleReject(sitter.sitterId)}
              style={{ backgroundColor: "#FFCCBC", padding: "8px 16px", border: "none", borderRadius: "4px", cursor: "pointer" }}
            >
              Reject
            </button>
          </div>
        </div>
      ))}

      {pending.length === 0 && <p>No pending sitter approvals!</p>}
    </div>
  );
}
```

## Related Features
- [Auth Feature](../auth/README.md)
- [Bookings Feature](../booking/README.md)

---

**Created**: May 9, 2026  
**Version**: 1.0  
**Status**: Complete
