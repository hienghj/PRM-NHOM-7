package com.example.recloopmart

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.example.recloopmart.databinding.ActivityProfileBinding
import com.example.recloopmart.data.local.AppDatabase
import com.example.recloopmart.data.repository.UserRepository
import com.example.recloopmart.data.network.Resource
import com.example.recloopmart.util.SharedPrefsManager
import com.example.recloopmart.util.toast
import com.example.recloopmart.util.ImagePickerHelper
import kotlinx.coroutines.launch

/**
 * ProfileActivity - Hiển thị thông tin user
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var prefsManager: SharedPrefsManager
    private lateinit var userRepository: UserRepository
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = SharedPrefsManager(this)
        database = AppDatabase.getDatabase(this)
        userRepository = UserRepository(database.userDao())

        // Load name immediately from SharedPreferences (synchronous)
        binding.tvName?.text = prefsManager.getUserName() ?: "User"
        
        setupUI()
        loadProfile()
    }

    private fun setupUI() {
        try {
            // Change avatar - both ImageView and TextView
            val changeAvatarHandler = View.OnClickListener {
                ImagePickerHelper.pickImageFromGallery(this)
            }
            
            binding.ivAvatar?.setOnClickListener(changeAvatarHandler)
            binding.tvChangeAvatar?.setOnClickListener(changeAvatarHandler)
        } catch (e: Exception) {
            android.util.Log.e("ProfileActivity", "Avatar views not found", e)
        }
        
        try {
            // Update info button
            binding.btnUpdateInfo?.setOnClickListener {
                toast("Cập nhật thông tin cá nhân")
                // TODO: Implement update profile functionality
            }
        } catch (e: Exception) {
            android.util.Log.e("ProfileActivity", "btnUpdateInfo not found", e)
        }

        try {
            // Change password button
            binding.btnChangePassword?.setOnClickListener {
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
            }
        } catch (e: Exception) {
            android.util.Log.e("ProfileActivity", "btnChangePassword not found", e)
        }

        try {
            // VIP Card button action
            val vipCardView = binding.root.findViewById<android.view.ViewGroup>(R.id.vipCard)
            val btnBuyVip = vipCardView?.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnBuyVip)
            btnBuyVip?.setOnClickListener {
                val intent = Intent(this, VipPurchaseActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        } catch (e: Exception) {
            android.util.Log.e("ProfileActivity", "VIP card not found", e)
        }

        try {
            // Logout button
            binding.btnLogout?.setOnClickListener {
                // Clear user session
                prefsManager.clearLoginData()
                
                // Navigate to login screen
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
        } catch (e: Exception) {
            android.util.Log.e("ProfileActivity", "btnLogout not found", e)
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
            android.util.Log.e("ProfileActivity", "Navigation icons not found", e)
        }

        try {
            // Bottom navigation
            binding.bottomNav?.selectedItemId = R.id.nav_profile
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
                    R.id.nav_profile -> true
                    else -> false
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ProfileActivity", "bottomNav not found", e)
        }

        try {
            // Attach AI bubble
            com.example.recloopmart.ui.AiAssistantHelper.attach(this)
        } catch (e: Exception) {
            android.util.Log.e("ProfileActivity", "AI Assistant failed", e)
        }
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            when (val result = userRepository.getProfile()) {
                is Resource.Success -> {
                    val user = result.data
                    
                    // Display user info
                    binding.tvName?.text = user?.fullName ?: prefsManager.getUserName() ?: "User"
                    
                    // TODO: Add other fields to layout if needed
                    // binding.tvEmail?.text = user?.email ?: prefsManager.getUserEmail() ?: ""
                    
                    android.util.Log.d("ProfileActivity", "Loaded user: ${user?.fullName}")
                }
                is Resource.Error -> {
                    // Fallback to SharedPreferences data
                    binding.tvName?.text = prefsManager.getUserName() ?: "User"
                    
                    // Try loading from Room DB by email
                    try {
                        val userEmail = prefsManager.getUserEmail()
                        if (!userEmail.isNullOrEmpty()) {
                            val user = database.userDao().getUserByEmail(userEmail)
                            if (user != null) {
                                binding.tvName?.text = user.fullName ?: "User"
                                android.util.Log.d("ProfileActivity", "Loaded from Room DB: ${user.fullName}")
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("ProfileActivity", "Failed to load from Room DB", e)
                    }
                }
                is Resource.Loading -> {
                    // Show loading state if needed
                }
            }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == ImagePickerHelper.REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            val imageUri = ImagePickerHelper.getImageUri(data)
            if (imageUri != null) {
                binding.ivAvatar?.setImageURI(imageUri)
                toast("Đã chọn ảnh đại diện")
                // TODO: Upload avatar to server
            }
        }
    }
}
