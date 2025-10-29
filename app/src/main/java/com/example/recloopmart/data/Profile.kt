package com.example.recloopmart.data

data class Profile(
    val fullName: String,
    val email: String,
    val phone: String,
    val day: Int,
    val month: Int,
    val year: Int,
    val gender: String
)

class ProfileRepository {
    fun load(): Profile = Profile(
        fullName = "Nguyễn Thùy Linh",
        email = "nguyen@example.com",
        phone = "+84 907 654 221",
        day = 1,
        month = 1,
        year = 1992,
        gender = "Nữ"
    )
}



