package com.example.recloopmart

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recloopmart.databinding.ActivityHomeBinding
import com.example.recloopmart.data.network.Resource
import com.example.recloopmart.ui.ProductAdapter
import com.example.recloopmart.util.toast
import com.example.recloopmart.viewmodel.HomeViewModel

/**
 * HomeActivity - Màn hình chính hiển thị danh sách sản phẩm
 * Sử dụng MVVM + RecyclerView
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
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
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(this@HomeActivity, 2)
            adapter = productAdapter
        }
    }

    private fun setupUI() {
        try {
            // Notification bell
            binding.ivBell?.setOnClickListener {
                startActivity(Intent(this, NotificationsActivity::class.java))
                overridePendingTransition(0, 0)
            }

            // Favorites icon
            binding.root.findViewById<android.widget.ImageView>(R.id.ivHeart)?.setOnClickListener {
                startActivity(Intent(this, FavoritesActivity::class.java))
                overridePendingTransition(0, 0)
            }

            // Cart icon
            binding.root.findViewById<android.widget.ImageView>(R.id.ivCart)?.setOnClickListener {
                startActivity(Intent(this, CartActivity::class.java))
                overridePendingTransition(0, 0)
            }

            // Discover button
            binding.btnDiscover?.setOnClickListener {
                toast("Khám phá sản phẩm mới!")
            }
        } catch (e: Exception) {
            android.util.Log.e("HomeActivity", "Navigation buttons not found", e)
        }

        try {
            // Bottom navigation
            binding.bottomNav?.selectedItemId = R.id.nav_home
            binding.bottomNav?.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_home -> true
                    R.id.nav_products -> {
                        startActivity(Intent(this, ProductManagementActivity::class.java))
                        overridePendingTransition(0, 0)
                        true
                    }
                    R.id.nav_messages -> {
                        startActivity(Intent(this, MessagesActivity::class.java))
                        overridePendingTransition(0, 0)
                        true
                    }
                    R.id.nav_profile -> {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        overridePendingTransition(0, 0)
                        true
                    }
                    else -> false
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("HomeActivity", "bottomNav not found", e)
        }

        try {
            // Attach AI assistant
            com.example.recloopmart.ui.AiAssistantHelper.attach(this)
        } catch (e: Exception) {
            android.util.Log.e("HomeActivity", "AI Assistant failed", e)
        }
    }

    private fun observeViewModel() {
        // Observe products list from API/Database
        viewModel.products.observe(this) { products ->
            productAdapter.submitList(products)
            
            // Show/hide empty state - TODO: Add tvEmptyState to layout
            if (products.isEmpty()) {
                // binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvProducts.visibility = View.GONE
            } else {
                // binding.tvEmptyState.visibility = View.GONE
                binding.rvProducts.visibility = View.VISIBLE
            }
        }

        // Observe loading state - TODO: Add progressBar to layout
        viewModel.loading.observe(this) { isLoading ->
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe errors
        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                toast(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh products when returning to this screen
        viewModel.loadProducts()
    }
}
