package com.example.recloopmart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recloopmart.databinding.ActivityNotificationsBinding
import com.example.recloopmart.util.toast

/**
 * NotificationsActivity - Thông báo
 */
class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Top navigation icons
        binding.ivHeart.setOnClickListener {
            startActivity(android.content.Intent(this, FavoritesActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        binding.ivCart.setOnClickListener {
            startActivity(android.content.Intent(this, CartActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        // Bottom navigation - No specific notifications item, so keep navigation active
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

        // Attach AI bubble
        com.example.recloopmart.ui.AiAssistantHelper.attach(this)
    }
}
