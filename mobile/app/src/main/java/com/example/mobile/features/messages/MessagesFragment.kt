package com.example.mobile.features.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.example.mobile.network.RetrofitClient
import com.example.mobile.util.PreferencesManager
import com.google.android.material.button.MaterialButton
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class MessagesFragment : Fragment() {

    private lateinit var prefsManager: PreferencesManager
    private lateinit var rvThreads: RecyclerView
    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: MaterialButton
    private lateinit var tvChatHeader: TextView
    private lateinit var tvChatSubheader: TextView

    private lateinit var threadAdapter: ThreadAdapter
    private lateinit var messageAdapter: MessageAdapter

    private var conversations: List<ConversationItem> = emptyList()
    private var activeConversation: ConversationItem? = null
    private var currentUserId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)
        prefsManager = PreferencesManager(requireContext())

        rvThreads = view.findViewById(R.id.rvMessageThreads)
        rvMessages = view.findViewById(R.id.rvMessageList)
        etMessage = view.findViewById(R.id.etMessageInput)
        btnSend = view.findViewById(R.id.btnSendMessage)
        tvChatHeader = view.findViewById(R.id.tvChatHeader)
        tvChatSubheader = view.findViewById(R.id.tvChatSubheader)

        currentUserId = prefsManager.getUserId()

        threadAdapter = ThreadAdapter(emptyList()) { item ->
            selectConversation(item)
        }

        rvThreads.layoutManager = LinearLayoutManager(requireContext())
        rvThreads.adapter = threadAdapter

        messageAdapter = MessageAdapter(emptyList(), currentUserId)
        rvMessages.layoutManager = LinearLayoutManager(requireContext())
        rvMessages.adapter = messageAdapter

        btnSend.setOnClickListener { sendMessage() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadConversations()
    }

    private fun loadConversations() {
        val token = prefsManager.getAuthToken() ?: return
        val role = prefsManager.getRole()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val bookingsResponse = withContext(Dispatchers.IO) {
                    if (role == "PET_SITTER") {
                        RetrofitClient.apiService.getSitterBookings("Bearer $token")
                    } else {
                        RetrofitClient.apiService.getMyBookings("Bearer $token", upcoming = false)
                    }
                }
                val threadsResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.listMessageThreads("Bearer $token")
                }

                if (!bookingsResponse.isSuccessful || !threadsResponse.isSuccessful) {
                    showToast("Failed to load conversations")
                    return@launch
                }

                val bookings = bookingsResponse.body() ?: emptyList()
                val threads = threadsResponse.body() ?: emptyList()
                val threadMap = threads.associateBy { it.otherUserId }

                val contactMap = linkedMapOf<String, ConversationItem>()
                bookings.forEach { booking ->
                    if (role == "PET_SITTER") {
                        val ownerId = booking.ownerId
                        if (!contactMap.containsKey(ownerId)) {
                            val thread = threadMap[ownerId]
                            contactMap[ownerId] = ConversationItem(
                                id = ownerId,
                                name = booking.ownerName ?: "Pet Owner",
                                roleLabel = "Pet Owner",
                                threadId = thread?.threadId,
                                lastMessage = thread?.lastMessage,
                                lastMessageAt = formatTimeLabel(thread?.lastMessageAt)
                            )
                        }
                    } else {
                        val sitterId = booking.sitterId ?: return@forEach
                        if (!contactMap.containsKey(sitterId)) {
                            val thread = threadMap[sitterId]
                            contactMap[sitterId] = ConversationItem(
                                id = sitterId,
                                name = booking.sitterName ?: "Pet Sitter",
                                roleLabel = "Pet Sitter",
                                threadId = thread?.threadId,
                                lastMessage = thread?.lastMessage,
                                lastMessageAt = formatTimeLabel(thread?.lastMessageAt)
                            )
                        }
                    }
                }

                if (!isAdded) return@launch

                conversations = contactMap.values.toList()
                threadAdapter.updateData(conversations)

                if (conversations.isNotEmpty()) {
                    selectConversation(conversations.first())
                }
            } catch (e: Exception) {
                showToast("Error loading conversations")
            }
        }
    }

    private fun selectConversation(item: ConversationItem) {
        activeConversation = item
        tvChatHeader.text = item.name
        tvChatSubheader.text = item.roleLabel
        loadMessages(item)
    }

    private fun loadMessages(item: ConversationItem) {
        val token = prefsManager.getAuthToken() ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val threadId = if (item.threadId != null) {
                    item.threadId
                } else {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.apiService.createMessageThread(
                            "Bearer $token",
                            CreateThreadRequest(item.id)
                        )
                    }
                    if (!response.isSuccessful || response.body() == null) {
                        showToast("Failed to create thread")
                        return@launch
                    }
                    val createdThread = response.body()!!
                    val updated = item.copy(
                        threadId = createdThread.threadId,
                        lastMessage = createdThread.lastMessage,
                        lastMessageAt = formatTimeLabel(createdThread.lastMessageAt)
                    )
                    conversations = conversations.map { existing ->
                        if (existing.id == item.id) updated else existing
                    }
                    if (!isAdded) return@launch

                    threadAdapter.updateData(conversations)
                    activeConversation = updated
                    createdThread.threadId
                }

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.listThreadMessages("Bearer $token", threadId)
                }

                if (!isAdded) return@launch

                if (response.isSuccessful) {
                    val messages = response.body() ?: emptyList()
                    val formatted = messages.map { msg ->
                        msg.copy(createdAt = formatTimeLabel(msg.createdAt))
                    }
                    messageAdapter.updateData(formatted)
                } else {
                    showToast("Failed to load messages")
                }
            } catch (e: Exception) {
                showToast("Error loading messages")
            }
        }
    }

    private fun sendMessage() {
        val token = prefsManager.getAuthToken() ?: return
        val conversation = activeConversation ?: return
        val content = etMessage.text.toString().trim()

        if (content.isEmpty()) return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val threadId = conversation.threadId ?: run {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.apiService.createMessageThread(
                            "Bearer $token",
                            CreateThreadRequest(conversation.id)
                        )
                    }
                    if (!response.isSuccessful || response.body() == null) {
                        showToast("Failed to create thread")
                        return@launch
                    }
                    val createdThread = response.body()!!
                    val updated = conversation.copy(
                        threadId = createdThread.threadId,
                        lastMessage = createdThread.lastMessage,
                        lastMessageAt = formatTimeLabel(createdThread.lastMessageAt)
                    )
                    conversations = conversations.map { existing ->
                        if (existing.id == conversation.id) updated else existing
                    }
                    if (!isAdded) return@launch

                    threadAdapter.updateData(conversations)
                    activeConversation = updated
                    createdThread.threadId
                }

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.sendMessage(
                        "Bearer $token",
                        threadId,
                        SendMessageRequest(content)
                    )
                }

                if (!isAdded) return@launch

                if (response.isSuccessful && response.body() != null) {
                    etMessage.setText("")
                    loadMessages(conversation)
                } else {
                    showToast("Failed to send message")
                }
            } catch (e: Exception) {
                showToast("Error sending message")
            }
        }
    }

    private fun showToast(message: String) {
        context?.let { Toast.makeText(it, message, Toast.LENGTH_SHORT).show() }
    }

    private fun formatTimeLabel(value: String?): String {
        if (value.isNullOrEmpty()) return ""
        val formats = listOf("yyyy-MM-dd'T'HH:mm:ss.SSSX", "yyyy-MM-dd'T'HH:mm:ssX")
        formats.forEach { pattern ->
            try {
                val parser = SimpleDateFormat(pattern, Locale.US)
                parser.timeZone = TimeZone.getTimeZone("UTC")
                val date = parser.parse(value) ?: return@forEach
                val formatter = SimpleDateFormat("MMM dd, h:mm a", Locale.US)
                return formatter.format(date)
            } catch (e: Exception) {
                // Try next format
            }
        }
        return value
    }

}
