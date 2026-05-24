package com.example.mobile.features.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R

class ThreadAdapter(
    private var threads: List<ConversationItem>,
    private val onSelect: (ConversationItem) -> Unit
) : RecyclerView.Adapter<ThreadAdapter.ThreadViewHolder>() {

    class ThreadViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvThreadName)
        val tvPreview: TextView = view.findViewById(R.id.tvThreadPreview)
        val tvTime: TextView = view.findViewById(R.id.tvThreadTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message_thread, parent, false)
        return ThreadViewHolder(view)
    }

    override fun onBindViewHolder(holder: ThreadViewHolder, position: Int) {
        val thread = threads[position]
        holder.tvName.text = thread.name
        holder.tvPreview.text = thread.lastMessage ?: "Start a conversation"
        holder.tvTime.text = thread.lastMessageAt ?: ""
        holder.itemView.setOnClickListener { onSelect(thread) }
    }

    override fun getItemCount() = threads.size

    fun updateData(newThreads: List<ConversationItem>) {
        threads = newThreads
        notifyDataSetChanged()
    }
}
