package com.example.recloopmart

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recloopmart.data.api.ProductCreateDTO
import com.example.recloopmart.data.repository.ProductRepository
import com.example.recloopmart.data.local.AppDatabase
import kotlinx.coroutines.launch
import com.example.recloopmart.databinding.ActivityProductManagementBinding
import com.example.recloopmart.ui.ProductAdapter
import com.example.recloopmart.util.toast
import com.example.recloopmart.viewmodel.ProductManageViewModel

/**
 * ProductManagementActivity - Quản lý sản phẩm của user
 */
class ProductManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductManagementBinding
    private val viewModel: ProductManageViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productRepository: ProductRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // DEBUG: Check full auth status
        com.example.recloopmart.util.AuthDebugHelper.checkAuthStatus(this, "ProductMgmt")
        
        // Initialize auth token from SharedPreferences
        val prefsManager = com.example.recloopmart.util.SharedPrefsManager(this)
        val token = prefsManager.getAuthToken()
        if (token != null) {
            com.example.recloopmart.data.network.ApiClient.setAuthToken(token)
            android.util.Log.d("ProductMgmt", "Token loaded from SharedPrefs: ${token.take(20)}...")
        } else {
            android.util.Log.w("ProductMgmt", "No token found in SharedPrefs")
        }
        
        // Initialize repository
        val database = AppDatabase.getDatabase(this)
        productRepository = ProductRepository(database.productDao())

        setupRecyclerView()
        setupUI()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            // Navigate to edit product
            // TODO: Create EditProductActivity
            toast("Edit product: ${product.title}")
        }

        // TODO: Add rvProducts to layout
        // binding.rvProducts.apply {
        //     layoutManager = LinearLayoutManager(this@ProductManagementActivity)
        //     adapter = productAdapter
        // }
    }

    private fun setupUI() {
        try {
            // Add product button
            binding.btnAddProduct?.setOnClickListener {
                showAddProductDialog()
            }
        } catch (e: Exception) {
            android.util.Log.e("ProductManagementActivity", "btnAddProduct not found", e)
        }

        try {
            // Top navigation icons
            binding.ivBell?.setOnClickListener {
                startActivity(Intent(this, NotificationsActivity::class.java))
                overridePendingTransition(0, 0)
            }

            binding.ivHeart?.setOnClickListener {
                startActivity(Intent(this, FavoritesActivity::class.java))
                overridePendingTransition(0, 0)
            }

            binding.ivCart?.setOnClickListener {
                startActivity(Intent(this, CartActivity::class.java))
                overridePendingTransition(0, 0)
            }
        } catch (e: Exception) {
            android.util.Log.e("ProductManagementActivity", "Navigation icons not found", e)
        }

        try {
            // Bottom navigation
            binding.bottomNav?.selectedItemId = R.id.nav_products
            binding.bottomNav?.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_home -> {
                        startActivity(Intent(this, HomeActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                        true
                    }
                    R.id.nav_products -> true
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
            android.util.Log.e("ProductManagementActivity", "bottomNav not found", e)
        }

        try {
            // Attach AI bubble
            com.example.recloopmart.ui.AiAssistantHelper.attach(this)
        } catch (e: Exception) {
            android.util.Log.e("ProductManagementActivity", "AI Assistant failed", e)
        }
    }

    private fun observeViewModel() {
        // Comment out for now until views are added to layout
        // viewModel.products.observe(this) { products ->
        //     productAdapter.submitList(products)
        //     
        //     if (products.isEmpty()) {
        //         binding.tvEmptyState.visibility = View.VISIBLE
        //         binding.rvProducts.visibility = View.GONE
        //     } else {
        //         binding.tvEmptyState.visibility = View.GONE
        //         binding.rvProducts.visibility = View.VISIBLE
        //     }
        // }

        // viewModel.loading.observe(this) { isLoading ->
        //     binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        // }

        // viewModel.error.observe(this) { error ->
        //     error?.let { toast(it) }
        // }
    }

    override fun onResume() {
        super.onResume()
        // viewModel.loadProducts()
    }

    private fun showAddProductDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.bottomsheet_add_product, null)
        
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDescriptions = view.findViewById<EditText>(R.id.etDescriptions)
        val etPrice = view.findViewById<EditText>(R.id.etPrice)
        val etCondition = view.findViewById<EditText>(R.id.etCondition)
        val etLocations = view.findViewById<EditText>(R.id.etLocations)
        val etCategory = view.findViewById<EditText>(R.id.etCategory)
        val btnCreate = view.findViewById<View>(R.id.btnCreate)
        val btnCancel = view.findViewById<View>(R.id.btnCancel)
        
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()
        
        btnCancel?.setOnClickListener { 
            dialog.dismiss() 
        }
        
        btnCreate?.setOnClickListener { _ ->
            android.util.Log.d("ProductMgmt", "btnCreate clicked")
            val title = etTitle?.text?.toString()?.trim() ?: ""
            val priceStr = etPrice?.text?.toString()?.trim() ?: ""
            
            if (TextUtils.isEmpty(title)) {
                toast("Vui lòng nhập tên sản phẩm")
                return@setOnClickListener
            }
            
            if (TextUtils.isEmpty(priceStr)) {
                toast("Vui lòng nhập giá sản phẩm")
                return@setOnClickListener
            }
            
            val price = try {
                priceStr.toDouble()
            } catch (e: Exception) {
                toast("Giá sản phẩm không hợp lệ")
                return@setOnClickListener
            }
            
            // Get optional fields
            val descriptions = etDescriptions?.text?.toString()?.trim()
            val condition = etCondition?.text?.toString()?.trim()
            val locations = etLocations?.text?.toString()?.trim()
            val categoryId = etCategory?.text?.toString()?.trim()?.toIntOrNull() ?: 0
            
            android.util.Log.d("ProductMgmt", "Creating product: title=$title, price=$price")
            
            // Create product DTO
            val productCreateDTO = ProductCreateDTO(
                title = title,
                descriptions = descriptions,
                price = price,
                condition = condition,
                locations = locations,
                categoryId = categoryId
            )
            
            android.util.Log.d("ProductMgmt", "ProductCreateDTO created: $productCreateDTO")
            
            // DEBUG: Check token before making request
            val currentToken = com.example.recloopmart.data.network.ApiClient.getAuthToken()
            if (currentToken != null) {
                android.util.Log.d("ProductMgmt", "✓ Token exists in ApiClient: ${currentToken.take(30)}...")
            } else {
                android.util.Log.e("ProductMgmt", "✗ NO TOKEN in ApiClient! This will cause 403!")
                android.util.Log.e("ProductMgmt", "Attempting to reload token...")
                com.example.recloopmart.util.AuthDebugHelper.forceReloadToken(this@ProductManagementActivity)
            }
            
            // Call API to create product
            lifecycleScope.launch {
                try {
                    android.util.Log.d("ProductMgmt", "Calling repository.createProduct")
                    val result = productRepository.createProduct(productCreateDTO)
                    android.util.Log.d("ProductMgmt", "Result received")
                    
                    when (result) {
                        is com.example.recloopmart.data.network.Resource.Success -> {
                            android.util.Log.d("ProductMgmt", "Product created successfully")
                            toast("✅ Đã tạo sản phẩm thành công!")
                            dialog.dismiss()
                        }
                        is com.example.recloopmart.data.network.Resource.Error -> {
                            android.util.Log.e("ProductMgmt", "Error: ${result.message}")
                            toast("❌ Lỗi: ${result.message ?: "Không thể tạo sản phẩm"}")
                        }
                        is com.example.recloopmart.data.network.Resource.Loading -> {
                            android.util.Log.d("ProductMgmt", "Loading...")
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ProductMgmt", "Exception: ", e)
                    toast("❌ Lỗi: ${e.message}")
                }
            }
        }
        
        dialog.show()
    }
}
