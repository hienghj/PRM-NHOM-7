package com.example.recloopmart.data

sealed class NotificationRow {
    data class Header(val title: String) : NotificationRow()
    data class Item(
        val id: String,
        val iconResId: Int,
        val title: String,
        val message: String,
        val timeLabel: String
    ) : NotificationRow()
}

class NotificationRepository {
    fun load(): List<NotificationRow> = listOf(
        NotificationRow.Header("Hôm nay"),
        NotificationRow.Item(
            id = "1",
            iconResId = android.R.drawable.ic_menu_directions,
            title = "Đơn hàng của bạn đã được giao",
            message = "Đơn hàng #GM12345 đã hoàn tất giao hàng thành công.",
            timeLabel = "15 phút trước"
        ),
        NotificationRow.Item(
            id = "2",
            iconResId = android.R.drawable.ic_dialog_email,
            title = "Tin nhắn mới từ Hỗ trợ GreenMart",
            message = "Chúng tôi đã nhận được yêu cầu của bạn và đang xử lý.",
            timeLabel = "30 phút trước"
        ),
        NotificationRow.Item(
            id = "3",
            iconResId = android.R.drawable.star_on,
            title = "Ưu đãi đặc biệt: Giảm 20% tất cả sản phẩm hữu cơ!",
            message = "Đừng bỏ lỡ các sản phẩm hữu cơ chất lượng cao với giá ưu đãi.",
            timeLabel = "1 giờ trước"
        ),
        NotificationRow.Header("Tuần trước"),
        NotificationRow.Item(
            id = "4",
            iconResId = android.R.drawable.ic_menu_compass,
            title = "Sản phẩm mới: Cà chua hữu cơ Đà Lạt",
            message = "Khám phá sản phẩm tươi ngon mới từ nông trại của chúng tôi.",
            timeLabel = "Thứ Ba tuần trước"
        ),
        NotificationRow.Item(
            id = "5",
            iconResId = android.R.drawable.ic_menu_edit,
            title = "Hãy đánh giá sản phẩm bạn đã mua",
            message = "Chia sẻ trải nghiệm của bạn về Xà lách thủy canh.",
            timeLabel = "Thứ Hai tuần trước"
        )
    )
}


