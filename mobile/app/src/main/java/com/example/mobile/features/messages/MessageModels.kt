package com.example.mobile.features.messages

data class MessageThread(
    val threadId: String,
    val otherUserId: String,
    val otherUserName: String,
    val otherUserRole: String,
    val lastMessage: String?,
    val lastMessageAt: String?
)

data class MessageItem(
    val messageId: String,
    val threadId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val createdAt: String
)

data class CreateThreadRequest(
    val otherUserId: String
)

data class SendMessageRequest(
    val content: String
)

data class ConversationItem(
    val id: String,
    val name: String,
    val roleLabel: String,
    val threadId: String?,
    val lastMessage: String?,
    val lastMessageAt: String?
)
