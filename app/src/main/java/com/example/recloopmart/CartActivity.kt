package com.example.recloopmart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recloopmart.databinding.ActivityCartBinding
import com.example.recloopmart.util.toast

/**
 * CartActivity - Giỏ hàng
 */
class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        try {
            // Back button
            binding.btnBack.setOnClickListener {
                finish()
            }
        } catch (e: Exception) {
            android.util.Log.e("CartActivity", "btnBack not found", e)
        }

        try {
            // Top navigation icons
            binding.ivBell?.setOnClickListener {
                startActivity(android.content.Intent(this, NotificationsActivity::class.java))
            }
        } catch (e: Exception) {
            android.util.Log.e("CartActivity", "ivBell not found", e)
        }

        try {
            binding.ivHeart?.setOnClickListener {
                startActivity(android.content.Intent(this, FavoritesActivity::class.java))
            }
        } catch (e: Exception) {
            android.util.Log.e("CartActivity", "ivHeart not found", e)
        }

        try {
            binding.ivCart?.setOnClickListener {
                // Already in cart activity
            }
        } catch (e: Exception) {
            android.util.Log.e("CartActivity", "ivCart not found", e)
        }

        try {
            // Bottom navigation - No specific cart item, so keep navigation active
            binding.bottomNav?.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_home -> {
                        startActivity(android.content.Intent(this, HomeActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                        true
                    }
                    R.id.nav_products -> {
                        startActivity(android.content.Intent(this, ProductManagementActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                        true
                    }
                    R.id.nav_messages -> {
                        startActivity(android.content.Intent(this, MessagesActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                        true
                    }
                    R.id.nav_profile -> {
                        startActivity(android.content.Intent(this, ProfileActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                        true
                    }
                    else -> false
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("CartActivity", "bottomNav not found", e)
        }

        try {
            // Attach AI bubble
            com.example.recloopmart.ui.AiAssistantHelper.attach(this)
        } catch (e: Exception) {
            android.util.Log.e("CartActivity", "AI Assistant failed", e)
        }
    }
}
