package com.example.recloopmart.data

data class FavoriteItem(
    val id: String,
    val name: String,
    val priceDisplay: String,
    val imageResId: Int
)

class FavoritesRepository {
    fun load(): List<FavoriteItem> = listOf(
        FavoriteItem("1", "Cây trầu bà lá xé", "VND", android.R.drawable.ic_menu_gallery),
        FavoriteItem("2", "Chậu cây lưỡi hổ", "VND", android.R.drawable.ic_menu_gallery)
    )
}


