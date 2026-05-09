# Messages Feature - Frontend Documentation

## Overview
The Messages feature enables direct messaging between pet owners and pet sitters. Users can create message threads and send/receive messages.

## Directory Structure
```
features/messages/
├── api.js           # API client functions
├── hooks/
│   ├── useMessages.js      # Message operations
│   └── useMessageThreads.js # (Optional) Thread-specific logic
├── components/
│   ├── MessageThread.js
│   ├── MessageList.js
│   ├── MessageForm.js
│   ├── ThreadList.js
│   └── README.md
└── README.md
```

## API Client

### Import
```javascript
import { messagesApi } from "@/features/messages/api";
```

### Methods

#### `messagesApi.listThreads()`
Get all message threads.
```javascript
const threads = await messagesApi.listThreads();
// Returns: [{ threadId, otherUserId, otherUserName, otherUserRole, lastMessage, lastMessageAt }, ...]
```

#### `messagesApi.createThread(otherUserId)`
Create or get existing thread with another user.
```javascript
const thread = await messagesApi.createThread("other-user-uuid");
// Returns: { threadId, otherUserId, otherUserName, ... }
```

#### `messagesApi.getThreadMessages(threadId)`
Get all messages in a thread.
```javascript
const messages = await messagesApi.getThreadMessages(threadId);
// Returns: [{ messageId, threadId, senderId, senderName, content, createdAt }, ...]
```

#### `messagesApi.sendMessage(threadId, content)`
Send message to thread.
```javascript
const message = await messagesApi.sendMessage(threadId, "Hi! Is your dog available tomorrow?");
// Returns: { messageId, threadId, senderId, senderName, content, createdAt }
```

## Hooks

### useMessages Hook
```javascript
import { useMessages } from "@/features/messages/hooks/useMessages";

const {
  threads,
  currentMessages,
  loading,
  error,
  listThreads,
  getThreadMessages,
  sendMessage
} = useMessages();
```

## Message Data Structure
```javascript
{
  messageId: "uuid",
  threadId: "uuid",
  senderId: "uuid",
  senderName: "John Doe",
  content: "Hi! Is your dog available tomorrow?",
  createdAt: "2024-03-15T10:30:00Z"
}
```

## Thread Data Structure
```javascript
{
  threadId: "uuid",
  otherUserId: "uuid",
  otherUserName: "Jane Smith",
  otherUserRole: "PET_SITTER",
  lastMessage: "Will do!",
  lastMessageAt: "2024-03-15T10:35:00Z"
}
```

## Example Component: MessageThread

```javascript
"use client";

import { useEffect, useState } from "react";
import { useMessages } from "@/features/messages/hooks/useMessages";
import { formatDateTime } from "@/shared/utils/formatting";
import { LoadingSpinner } from "@/shared/components/Banners";

export default function MessageThread({ threadId }) {
  const { currentMessages, loading, getThreadMessages, sendMessage } = useMessages();
  const [newMessage, setNewMessage] = useState("");

  useEffect(() => {
    getThreadMessages(threadId);
  }, [threadId]);

  const handleSend = async (e) => {
    e.preventDefault();
    if (newMessage.trim()) {
      await sendMessage(threadId, newMessage);
      setNewMessage("");
    }
  };

  if (loading) return <LoadingSpinner message="Loading messages..." />;

  return (
    <div>
      <div style={{ borderBottom: "1px solid #DDD", minHeight: "400px", padding: "16px" }}>
        {currentMessages.map(msg => (
          <div key={msg.messageId} style={{ marginBottom: "12px" }}>
            <p style={{ margin: "0 0 4px 0", fontWeight: "bold" }}>
              {msg.senderName}
            </p>
            <p style={{ margin: "0 0 4px 0" }}>{msg.content}</p>
            <p style={{ margin: 0, fontSize: "12px", color: "#999" }}>
              {formatDateTime(msg.createdAt)}
            </p>
          </div>
        ))}
      </div>

      <form onSubmit={handleSend} style={{ padding: "16px", display: "flex", gap: "8px" }}>
        <textarea
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          placeholder="Type your message..."
          rows="3"
          style={{ flex: 1 }}
        />
        <button type="submit">Send</button>
      </form>
    </div>
  );
}
```

## Related Features
- [Bookings Feature](../booking/README.md)
- [Sitters Feature](../sitters/README.md)

---

**Created**: May 9, 2026  
**Version**: 1.0  
**Status**: Complete
