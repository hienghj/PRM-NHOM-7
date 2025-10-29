package com.example.recloopmart.data

import androidx.annotation.DrawableRes

data class Conversation(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timeLabel: String,
    val unreadCount: Int,
    @DrawableRes val avatarResId: Int
)

class ConversationRepository {
    fun load(): List<Conversation> = listOf(
        Conversation("1", "Mai Phương Thảo", "Vâng, tôi đã nhận được thông tin rồi", "14:30", 2, android.R.drawable.sym_def_app_icon),
        Conversation("2", "Nguyễn Văn A", "Được rồi, hẹn gặp bạn vào ngày mai.", "Hôm qua", 0, android.R.drawable.sym_def_app_icon),
        Conversation("3", "Nhóm hỗ trợ GreenM", "Chúng tôi đã nhận được yêu cầu của bạn.", "Thứ Ba", 5, android.R.drawable.sym_def_app_icon),
        Conversation("4", "Trần Thị B", "Sản phẩm của bạn đã được giao.", "2 tuần trước", 0, android.R.drawable.sym_def_app_icon),
        Conversation("5", "Hoàng Quốc Cường", "Bạn có thể gửi thêm chi tiết được không?", "Tháng trước", 0, android.R.drawable.sym_def_app_icon)
    )
}



