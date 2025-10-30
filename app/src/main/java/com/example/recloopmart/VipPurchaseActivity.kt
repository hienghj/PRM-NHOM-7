package com.example.recloopmart

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.recloopmart.databinding.ActivityVipPurchaseBinding
import com.example.recloopmart.viewmodel.PaymentMethod
import com.example.recloopmart.viewmodel.VipPurchaseViewModel

class VipPurchaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVipPurchaseBinding
    private val viewModel: VipPurchaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVipPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            val i = android.content.Intent(this, HomeActivity::class.java)
            i.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(i)
            overridePendingTransition(0,0)
        }

        // bell icon opens notifications
        val bell = findViewById<android.widget.ImageView>(R.id.ivBell)
        bell?.setOnClickListener {
            val i = android.content.Intent(this, NotificationsActivity::class.java)
            startActivity(i)
            overridePendingTransition(0, 0)
        }
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

        fun updateChecks(method: PaymentMethod) {
            val pm = binding.paymentMethods
            pm.pmCardCheck.setColorFilter(resources.getColor(if (method == PaymentMethod.CARD) R.color.green_primary else R.color.gray_light))
            pm.pmBankCheck.setColorFilter(resources.getColor(if (method == PaymentMethod.BANK) R.color.green_primary else R.color.gray_light))
            pm.pmEwalletCheck.setColorFilter(resources.getColor(if (method == PaymentMethod.EWALLET) R.color.green_primary else R.color.gray_light))
        }

        binding.paymentMethods.pmCard.setOnClickListener { viewModel.select(PaymentMethod.CARD) }
        binding.paymentMethods.pmBank.setOnClickListener { viewModel.select(PaymentMethod.BANK) }
        binding.paymentMethods.pmEwallet.setOnClickListener { viewModel.select(PaymentMethod.EWALLET) }

        viewModel.selected.observe(this) { updateChecks(it) }

        binding.btnVipBuyNow.setOnClickListener {
            android.widget.Toast.makeText(this, "Thanh toán đang được xử lý", android.widget.Toast.LENGTH_SHORT).show()
        }

        // Bottom navigation - No specific VIP purchase item, so keep navigation active
        binding.bottomNav.setOnItemSelectedListener {
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


