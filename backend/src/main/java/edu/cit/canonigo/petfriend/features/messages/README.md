# Messages Feature API

## Overview
The messages feature provides APIs for:
- Creating and managing message threads between pet owners and sitters
- Sending and receiving messages within threads
- Listing conversations and message history

## API Endpoints

### Message Threads

#### List All Threads
```
GET /api/messages/threads
```
**Authentication:** Required (any role)

**Response:** 200 OK
```json
[
  {
    "threadId": "uuid",
    "otherUserId": "uuid",
    "otherUserName": "John Doe",
    "otherUserRole": "PET_SITTER",
    "lastMessage": "Thanks for booking me!",
    "lastMessageAt": "2024-03-15T14:30:00Z"
  }
]
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated

---

#### Create or Get Thread
```
POST /api/messages/threads
```
**Authentication:** Required (any role)

**Request Body:**
```json
{
  "otherUserId": "uuid"
}
```

**Response:** 200 OK
```json
{
  "threadId": "uuid",
  "otherUserId": "uuid",
  "otherUserName": "John Doe",
  "otherUserRole": "PET_SITTER",
  "lastMessage": "Thanks for booking me!",
  "lastMessageAt": "2024-03-15T14:30:00Z"
}
```

**Validation Rules:**
- Other user must exist
- Other user must have different role (owner ↔ sitter only)
- Must have at least one booking between users
- Returns existing thread if one already exists

**Error Responses:**
- `400 Bad Request` - Invalid roles or no common booking
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - No booking exists between users
- `404 Not Found` - Other user doesn't exist

---

### Messages

#### List Messages in Thread
```
GET /api/messages/threads/{threadId}/messages
```
**Authentication:** Required (must be thread participant)

**Path Parameters:**
- `threadId` - UUID of the message thread

**Response:** 200 OK
```json
[
  {
    "messageId": "uuid",
    "threadId": "uuid",
    "senderId": "uuid",
    "senderName": "Jane Smith",
    "content": "Hi! I'd like to book your services for next Friday.",
    "createdAt": "2024-03-15T14:00:00Z"
  },
  {
    "messageId": "uuid",
    "threadId": "uuid",
    "senderId": "uuid",
    "senderName": "John Doe",
    "content": "Sounds great! I'm available that day.",
    "createdAt": "2024-03-15T14:30:00Z"
  }
]
```

**Error Responses:**
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not a participant of the thread
- `404 Not Found` - Thread doesn't exist

---

#### Send Message
```
POST /api/messages/threads/{threadId}/messages
```
**Authentication:** Required (must be thread participant)

**Path Parameters:**
- `threadId` - UUID of the message thread

**Request Body:**
```json
{
  "content": "Thanks for helping out!"
}
```

**Response:** 201 Created
```json
{
  "messageId": "uuid",
  "threadId": "uuid",
  "senderId": "uuid",
  "senderName": "Jane Smith",
  "content": "Thanks for helping out!",
  "createdAt": "2024-03-15T15:00:00Z"
}
```

**Validation Rules:**
- Content must not be blank
- Sender must be participant of thread
- Booking must still exist between participants
- Content is trimmed before storage

**Error Responses:**
- `400 Bad Request` - Content is blank
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Not a participant or no active booking
- `404 Not Found` - Thread doesn't exist

---

## Data Models

### MessageThread Entity
```
- threadId: UUID (primary key)
- ownerId: UUID (foreign key to User with role PET_OWNER)
- sitterId: UUID (foreign key to User with role PET_SITTER)
- lastMessageAt: Instant (timestamp of last message)
- createdAt: Instant
- updatedAt: Instant
```

### Message Entity
```
- messageId: UUID (primary key)
- threadId: UUID (foreign key to MessageThread)
- senderId: UUID (foreign key to User)
- content: String (message text)
- createdAt: Instant
- updatedAt: Instant
```

---

## Business Logic

### Thread Creation
1. Only owner ↔ sitter threads allowed (different roles required)
2. Both users must have an active booking together
3. If thread already exists, returns existing thread
4. Sorted by last message timestamp (most recent first)

### Message Sending
1. Only thread participants can send/receive messages
2. Messages can only be sent if booking still exists
3. Content is trimmed of whitespace
4. Thread's `lastMessageAt` is updated

### Authorization
- Only thread participants can view messages
- Only sender can see their own messages
- Participants verified via owner/sitter IDs in thread

---

## Authorization

| Endpoint | Participants | Notes |
|----------|--------------|-------|
| `GET /api/messages/threads` | Any authenticated user | Returns threads for current user |
| `POST /api/messages/threads` | Any authenticated user | Requires booking with other user |
| `GET /api/messages/threads/{id}/messages` | Thread participants | Both owner and sitter |
| `POST /api/messages/threads/{id}/messages` | Thread participants | Requires active booking |

---

## Error Codes

| Code | Message | Cause |
|------|---------|-------|
| 400 | Invalid roles for messaging | Users have same role |
| 400 | Invalid recipient role | Role mismatch in creation |
| 401 | Unauthorized | No authentication token |
| 403 | Forbidden | Not a thread participant |
| 403 | Messaging is limited to existing bookings | No booking between users |
| 404 | User not found | Other user ID invalid |
| 404 | Thread not found | Thread ID invalid |

---

## Created Date
May 9, 2026

## Version
1.0
