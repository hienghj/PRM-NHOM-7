package com.example.recloopmart

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recloopmart.databinding.ActivityFavoritesBinding
import com.example.recloopmart.data.local.AppDatabase
import com.example.recloopmart.data.repository.ProductRepository
import com.example.recloopmart.ui.ProductAdapter
import com.example.recloopmart.util.toast
import com.example.recloopmart.viewmodel.FavoritesViewModel
import kotlinx.coroutines.launch

/**
 * FavoritesActivity - Màn hình danh sách yêu thích
 */
class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupUI()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            // Navigate to product detail
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("product_id", product.id.toString())
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.rvFavorites.apply {
            layoutManager = GridLayoutManager(this@FavoritesActivity, 2)
            adapter = productAdapter
        }
    }

    private fun setupUI() {
        try {
            // Back button
            binding.btnBack.setOnClickListener {
                finish()
            }
        } catch (e: Exception) {
            android.util.Log.e("FavoritesActivity", "btnBack not found", e)
        }

        try {
            // Top navigation icons
            binding.ivBell?.setOnClickListener {
                startActivity(Intent(this, NotificationsActivity::class.java))
                overridePendingTransition(0, 0)
            }

            binding.ivHeart?.setOnClickListener {
                // Already in favorites activity
            }

            binding.ivCart?.setOnClickListener {
                startActivity(Intent(this, CartActivity::class.java))
                overridePendingTransition(0, 0)
            }
        } catch (e: Exception) {
            android.util.Log.e("FavoritesActivity", "Navigation icons not found", e)
        }

        try {
            // Bottom navigation - No specific favorites item, so keep navigation active
            binding.bottomNav?.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_home -> {
                        startActivity(Intent(this, HomeActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                        true
                    }
                    R.id.nav_products -> {
                        startActivity(Intent(this, ProductManagementActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                        true
                    }
                    R.id.nav_messages -> {
                        startActivity(Intent(this, MessagesActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                        true
                    }
                    R.id.nav_profile -> {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                        true
                    }
                    else -> false
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("FavoritesActivity", "bottomNav not found", e)
        }

        try {
            // Attach AI bubble
            com.example.recloopmart.ui.AiAssistantHelper.attach(this)
        } catch (e: Exception) {
            android.util.Log.e("FavoritesActivity", "AI Assistant failed", e)
        }
    }

    private fun observeViewModel() {
        // Observe favourites list
        viewModel.favourites.observe(this) { favourites ->
            // Load products for each favourite
            lifecycleScope.launch {
                val database = AppDatabase.getDatabase(this@FavoritesActivity)
                val productRepo = ProductRepository(database.productDao())
                
                val products = favourites.mapNotNull { fav ->
                    val result = productRepo.getProductById(fav.productId)
                    if (result is com.example.recloopmart.data.network.Resource.Success) {
                        result.data
                    } else null
                }
                
                productAdapter.submitList(products)
                
                // Show/hide empty state
                if (products.isEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.rvFavorites.visibility = View.GONE
                } else {
                    binding.tvEmptyState.visibility = View.GONE
                    binding.rvFavorites.visibility = View.VISIBLE
                }
            }
        }

        // Observe loading - TODO: Add progressBar to layout
        viewModel.loading.observe(this) { isLoading ->
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe errors
        viewModel.error.observe(this) { error ->
            error?.let { toast(it) }
        }
    }
}
