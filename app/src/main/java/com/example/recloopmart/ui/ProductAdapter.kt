package com.example.recloopmart.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.recloopmart.R
import com.example.recloopmart.data.local.ProductEntity
import com.example.recloopmart.databinding.ItemProductBinding
import com.example.recloopmart.util.toVndFormat

/**
 * RecyclerView Adapter for Product list
 * Uses DiffUtil for efficient updates
 */
class ProductAdapter(
    private val onItemClick: (ProductEntity) -> Unit
) : ListAdapter<ProductEntity, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductEntity) {
            binding.apply {
                // Set product data
                tvProductName.text = product.title
                tvProductPrice.text = product.price.toVndFormat()
                tvProductLocation.text = product.locations ?: "N/A"
                tvProductCondition.text = product.condition ?: "N/A"
                
                // Load image using Coil
                ivProductImage.load(product.imageUrls?.firstOrNull()) {
                    crossfade(true)
                }
                
                // Set category badge if available
                if (product.categoryName != null) {
                    tvCategoryBadge?.text = product.categoryName
                } else {
                    tvCategoryBadge?.text = ""
                }
                
                // Click listener
                root.setOnClickListener {
                    onItemClick(product)
                }
            }
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<ProductEntity>() {
        override fun areItemsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
            return oldItem == newItem
        }
    }
}



