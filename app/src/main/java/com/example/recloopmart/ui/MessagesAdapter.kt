package com.example.recloopmart.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recloopmart.data.Conversation
import com.example.recloopmart.databinding.ItemConversationBinding

class MessagesAdapter : ListAdapter<Conversation, MessagesAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Conversation>() {
            override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean = oldItem == newItem
        }
    }

    inner class VH(val binding: ItemConversationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Conversation) {
            binding.ivAvatar.setImageResource(item.avatarResId)
            binding.tvName.text = item.name
            binding.tvLastMessage.text = item.lastMessage
            binding.tvTime.text = item.timeLabel

            if (item.unreadCount > 0) {
                binding.tvUnread.visibility = View.VISIBLE
                binding.tvUnread.text = item.unreadCount.toString()
            } else {
                binding.tvUnread.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}



