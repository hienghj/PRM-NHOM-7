package com.example.recloopmart

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.recloopmart.databinding.ActivityProductDetailBinding
import com.example.recloopmart.util.toast
import com.example.recloopmart.util.toVndFormat
import com.example.recloopmart.viewmodel.ProductDetailViewModel
import com.example.recloopmart.data.repository.CommentRepository
import com.example.recloopmart.data.repository.ReviewRepository
import com.example.recloopmart.data.local.AppDatabase
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private val viewModel: ProductDetailViewModel by viewModels()
    private var currentProductId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button
        binding.btnBack.setOnClickListener {
            val i = android.content.Intent(this, HomeActivity::class.java)
            i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(i)
            overridePendingTransition(0,0)
        }
        
        // Notification button
        binding.ivBell.setOnClickListener {
            val i = android.content.Intent(this, NotificationsActivity::class.java)
            i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(i)
            overridePendingTransition(0,0)
        }

        val productId = intent.getStringExtra("product_id").orEmpty()
        
        // Observe product data from API/Database
        viewModel.product.observe(this) { product ->
            product?.let {
                // Load first image or use placeholder
                val imageUrl = it.imageUrls?.firstOrNull()
                if (imageUrl != null) {
                    binding.ivImage.load(imageUrl) {
                        crossfade(true)
                    }
                } else {
                    // Set default image or leave empty
                    binding.ivImage.setImageDrawable(null)
                }
                
                binding.tvName.text = it.title
                binding.tvPrice.text = it.price.toVndFormat()
                
                // Show description if exists
                binding.tvDescription.text = it.descriptions ?: "Không có mô tả"
            }
        }
        
        // Observe loading state
        viewModel.loading.observe(this) { isLoading ->
            // Show/hide progress bar if you have one
            // binding.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Observe errors
        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                toast(it)
            }
        }
        
        // Load product from API
        viewModel.load(productId)

        // Favorite toggle
        binding.btnFavorite.setOnClickListener { 
            viewModel.toggleFavorite() 
        }
        
        viewModel.favorite.observe(this) { isFav ->
            if (isFav) {
                binding.btnFavorite.icon = getDrawable(R.drawable.ic_heart_filled)
                binding.btnFavorite.iconTint = android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE)
            } else {
                binding.btnFavorite.icon = getDrawable(R.drawable.ic_heart_outline)
                binding.btnFavorite.iconTint = android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE)
            }
        }

        // AI Compare button
        binding.btnAiCompare.setOnClickListener {
            toast("Tính năng so sánh AI đang được phát triển")
        }

        // Send comment button
        binding.btnSendComment.setOnClickListener {
            val commentText = binding.etComment.text?.toString()?.trim() ?: ""
            if (commentText.isEmpty()) {
                toast("Vui lòng nhập bình luận")
            } else {
                // TODO: Implement send comment functionality
                toast("Gửi bình luận: $commentText")
                binding.etComment.text?.clear()
            }
        }

        // Bottom Navigation
        binding.bottomNav.selectedItemId = R.id.nav_products
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    val intent = android.content.Intent(this, HomeActivity::class.java)
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_products -> true
                R.id.nav_messages -> {
                    val intent = android.content.Intent(this, MessagesActivity::class.java)
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_profile -> {
                    val intent = android.content.Intent(this, ProfileActivity::class.java)
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

        // Heart/Cart top actions
        findViewById<android.widget.ImageView>(R.id.ivHeart)?.setOnClickListener {
            val i = android.content.Intent(this, FavoritesActivity::class.java)
            startActivity(i)
            overridePendingTransition(0, 0)
        }
        findViewById<android.widget.ImageView>(R.id.ivCart)?.setOnClickListener {
            val i = android.content.Intent(this, CartActivity::class.java)
            startActivity(i)
            overridePendingTransition(0, 0)
        }
        findViewById<android.widget.ImageView>(R.id.ivBell)?.setOnClickListener {
            val i = android.content.Intent(this, NotificationsActivity::class.java)
            startActivity(i)
            overridePendingTransition(0, 0)
        }

        // Attach AI bubble
        com.example.recloopmart.ui.AiAssistantHelper.attach(this)
        
        // Load comments & reviews
        currentProductId = productId.toIntOrNull() ?: 0
        if (currentProductId > 0) {
            loadCommentsAndReviews()
        }
    }
    
    /**
     * Load comments and reviews for this product
     */
    private fun loadCommentsAndReviews() {
        val database = AppDatabase.getDatabase(this)
        val commentRepo = CommentRepository(database.commentDao())
        val reviewRepo = ReviewRepository(database.reviewDao())
        
        // Load comments
        lifecycleScope.launch {
            commentRepo.getCommentsByProductId(currentProductId).collect { resource ->
                when (resource) {
                    is com.example.recloopmart.data.network.Resource.Success -> {
                        val comments = resource.data ?: emptyList()
                        // TODO: Display comments in RecyclerView
                    }
                    is com.example.recloopmart.data.network.Resource.Error -> {
                        // toast(resource.message ?: "Lỗi load comments")
                    }
                    is com.example.recloopmart.data.network.Resource.Loading -> {
                        // Show loading
                    }
                }
            }
        }
        
        // Load reviews
        lifecycleScope.launch {
            reviewRepo.getReviewsByProductId(currentProductId).collect { resource ->
                when (resource) {
                    is com.example.recloopmart.data.network.Resource.Success -> {
                        val reviews = resource.data ?: emptyList()
                        // Calculate average rating
                        val avgRating = if (reviews.isNotEmpty()) {
                            reviews.mapNotNull { it.rating }.average()
                        } else 0.0
                        // TODO: Display reviews in RecyclerView
                    }
                    is com.example.recloopmart.data.network.Resource.Error -> {
                        // toast(resource.message ?: "Lỗi load reviews")
                    }
                    is com.example.recloopmart.data.network.Resource.Loading -> {
                        // Show loading
                    }
                }
            }
        }
    }
}
