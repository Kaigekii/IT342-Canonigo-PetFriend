package com.example.mobile.features.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R

class MessageAdapter(
    private var messages: List<MessageItem>,
    private val currentUserId: String?
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    companion object {
        private const val VIEW_TYPE_MINE = 1
        private const val VIEW_TYPE_THEIRS = 2
    }

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvContent: TextView = view.findViewById(R.id.tvMessageContent)
        val tvTime: TextView = view.findViewById(R.id.tvMessageTime)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == currentUserId) VIEW_TYPE_MINE else VIEW_TYPE_THEIRS
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layout = if (viewType == VIEW_TYPE_MINE) {
            R.layout.item_message_bubble_mine
        } else {
            R.layout.item_message_bubble_theirs
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.tvContent.text = message.content
        holder.tvTime.text = message.createdAt
    }

    override fun getItemCount() = messages.size

    fun updateData(newMessages: List<MessageItem>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}
