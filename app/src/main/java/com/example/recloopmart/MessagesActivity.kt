package com.example.recloopmart

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.recloopmart.databinding.ActivityMessagesBinding
import com.example.recloopmart.util.toast

/**
 * MessagesActivity - Danh sách tin nhắn/hội thoại
 */
class MessagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // Top navigation icons
        binding.ivBell?.setOnClickListener {
            startActivity(android.content.Intent(this, NotificationsActivity::class.java))
            overridePendingTransition(0, 0)
        }

        binding.ivHeart?.setOnClickListener {
            startActivity(android.content.Intent(this, FavoritesActivity::class.java))
            overridePendingTransition(0, 0)
        }

        binding.ivCart?.setOnClickListener {
            startActivity(android.content.Intent(this, CartActivity::class.java))
            overridePendingTransition(0, 0)
        }

        // Bottom navigation
        binding.bottomNav?.selectedItemId = R.id.nav_messages
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
                R.id.nav_messages -> true
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
