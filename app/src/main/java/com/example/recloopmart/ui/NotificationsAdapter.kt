package com.example.recloopmart.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recloopmart.data.NotificationRow
import com.example.recloopmart.databinding.ItemNotificationBinding
import com.example.recloopmart.databinding.ItemNotificationHeaderBinding

class NotificationsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<NotificationRow>()

    fun submit(list: List<NotificationRow>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is NotificationRow.Header -> 0
        is NotificationRow.Item -> 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == 0) HeaderVH(
            ItemNotificationHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        ) else ItemVH(
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is NotificationRow.Header -> (holder as HeaderVH).bind(item)
            is NotificationRow.Item -> (holder as ItemVH).bind(item)
        }
    }

    class HeaderVH(private val binding: ItemNotificationHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(h: NotificationRow.Header) { binding.root.text = h.title }
    }

    class ItemVH(private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(i: NotificationRow.Item) {
            binding.ivIcon.setImageResource(i.iconResId)
            binding.tvTitle.text = i.title
            binding.tvMessage.text = i.message
            binding.tvTime.text = i.timeLabel
        }
    }
}


