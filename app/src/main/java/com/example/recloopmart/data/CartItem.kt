package com.example.recloopmart.data

data class CartItem(
    val id: String,
    val name: String,
    val priceDisplay: String,
    val quantity: Int,
    val imageResId: Int
)

class CartRepository {
    fun load(): List<CartItem> = listOf(
        CartItem("1", "Đất hữu cơ", "VND", 2, android.R.drawable.ic_menu_crop),
        CartItem("2", "Bình tưới", "VND", 1, android.R.drawable.ic_menu_crop)
    )
}


