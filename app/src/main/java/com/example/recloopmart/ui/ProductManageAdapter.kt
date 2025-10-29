package com.example.recloopmart.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recloopmart.data.Product
import com.example.recloopmart.data.ProductStatus
import com.example.recloopmart.databinding.ItemProductManageBinding

class ProductManageAdapter(
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit,
    private val onOpen: (Product) -> Unit
) : ListAdapter<Product, ProductManageAdapter.ProductViewHolder>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem == newItem
        }
    }

    inner class ProductViewHolder(val binding: ItemProductManageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Product) {
            binding.ivImage.setImageResource(item.imageResId)
            binding.tvName.text = item.name
            binding.tvPrice.text = item.priceDisplay
            binding.tvQuantity.text = item.quantityDisplay

            val statusText = if (item.status == ProductStatus.PUBLISHED) "Đã xuất bản" else "Đang soạn thảo"
            val statusIcon = if (item.status == ProductStatus.PUBLISHED) android.R.drawable.presence_online else android.R.drawable.presence_invisible
            binding.ivStatusIcon.setImageResource(statusIcon)
            binding.tvStatus.text = statusText

            binding.btnEdit.setOnClickListener { onEdit(item) }
            binding.btnDelete.setOnClickListener { onDelete(item) }
            binding.root.setOnClickListener { onOpen(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductManageBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}



