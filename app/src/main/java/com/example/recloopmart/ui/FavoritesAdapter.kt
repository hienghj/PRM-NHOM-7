package com.example.recloopmart.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recloopmart.data.FavoriteItem
import com.example.recloopmart.databinding.ItemFavoriteBinding

class FavoritesAdapter(
    private val onRemove: (FavoriteItem) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.VH>() {

    private val items = mutableListOf<FavoriteItem>()

    fun submit(list: List<FavoriteItem>) {
        items.clear(); items.addAll(list); notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    inner class VH(private val binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FavoriteItem) {
            binding.ivImage.setImageResource(item.imageResId)
            binding.tvName.text = item.name
            binding.tvPrice.text = item.priceDisplay
            binding.btnRemove.setOnClickListener { onRemove(item) }
        }
    }
}


