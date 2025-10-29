package com.example.recloopmart.util

import java.util.regex.Pattern

/**
 * Password Validator với các ràng buộc bảo mật cao nhất
 * Tuân thủ các tiêu chuẩn bảo mật quốc tế
 */
object PasswordValidator {
    
    // Các pattern regex cho validation
    private val UPPERCASE_PATTERN = Pattern.compile("[A-Z]")
    private val LOWERCASE_PATTERN = Pattern.compile("[a-z]")
    private val DIGIT_PATTERN = Pattern.compile("[0-9]")
    private val SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]")
    private val COMMON_PATTERNS = listOf(
        "123456", "password", "qwerty", "abc123", "password123",
        "admin", "letmein", "welcome", "monkey", "dragon",
        "master", "hello", "login", "princess", "qwertyuiop"
    )
    
    /**
     * Kết quả validation mật khẩu
     */
    data class PasswordValidationResult(
        val isValid: Boolean,
        val errors: List<String> = emptyList(),
        val strength: PasswordStrength = PasswordStrength.WEAK,
        val score: Int = 0
    )
    
    /**
     * Độ mạnh của mật khẩu
     */
    enum class PasswordStrength(val displayName: String, val color: String) {
        WEAK("Rất yếu", "#FF5252"),
        FAIR("Yếu", "#FF9800"), 
        GOOD("Trung bình", "#FFC107"),
        STRONG("Mạnh", "#4CAF50"),
        VERY_STRONG("Rất mạnh", "#2E7D32")
    }
    
    /**
     * Validate mật khẩu với các ràng buộc bảo mật cao
     */
    fun validatePassword(password: String): PasswordValidationResult {
        val errors = mutableListOf<String>()
        
        // Kiểm tra độ dài tối thiểu
        if (password.length < 8) {
            errors.add("Mật khẩu phải có ít nhất 8 ký tự")
        }
        
        // Kiểm tra độ dài tối đa (tránh DoS)
        if (password.length > 128) {
            errors.add("Mật khẩu không được vượt quá 128 ký tự")
        }
        
        // Kiểm tra chữ hoa
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            errors.add("Mật khẩu phải chứa ít nhất 1 chữ cái viết hoa (A-Z)")
        }
        
        // Kiểm tra chữ thường
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            errors.add("Mật khẩu phải chứa ít nhất 1 chữ cái viết thường (a-z)")
        }
        
        // Kiểm tra số
        if (!DIGIT_PATTERN.matcher(password).find()) {
            errors.add("Mật khẩu phải chứa ít nhất 1 chữ số (0-9)")
        }
        
        // Kiểm tra ký tự đặc biệt
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            errors.add("Mật khẩu phải chứa ít nhất 1 ký tự đặc biệt (!@#$%^&*...)")
        }
        
        // Kiểm tra mật khẩu phổ biến
        if (isCommonPassword(password)) {
            errors.add("Mật khẩu này quá phổ biến, vui lòng chọn mật khẩu khác")
        }
        
        // Kiểm tra chuỗi lặp lại
        if (hasRepeatingCharacters(password)) {
            errors.add("Mật khẩu không được chứa chuỗi ký tự lặp lại (ví dụ: aaa, 111)")
        }
        
        // Kiểm tra chuỗi liên tiếp
        if (hasSequentialCharacters(password)) {
            errors.add("Mật khẩu không được chứa chuỗi liên tiếp (ví dụ: abc, 123)")
        }
        
        // Kiểm tra khoảng trắng
        if (password.contains(" ")) {
            errors.add("Mật khẩu không được chứa khoảng trắng")
        }
        
        // Tính điểm độ mạnh
        val strength = calculatePasswordStrength(password)
        val score = calculatePasswordScore(password)
        
        return PasswordValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            strength = strength,
            score = score
        )
    }
    
    /**
     * Kiểm tra mật khẩu phổ biến
     */
    private fun isCommonPassword(password: String): Boolean {
        val lowerPassword = password.lowercase()
        return COMMON_PATTERNS.any { common ->
            lowerPassword.contains(common.lowercase()) || 
            common.lowercase().contains(lowerPassword)
        }
    }
    
    /**
     * Kiểm tra ký tự lặp lại
     */
    private fun hasRepeatingCharacters(password: String): Boolean {
        var count = 1
        for (i in 1 until password.length) {
            if (password[i] == password[i - 1]) {
                count++
                if (count >= 3) return true
            } else {
                count = 1
            }
        }
        return false
    }
    
    /**
     * Kiểm tra chuỗi liên tiếp
     */
    private fun hasSequentialCharacters(password: String): Boolean {
        val lowerPassword = password.lowercase()
        
        // Kiểm tra chuỗi số liên tiếp
        for (i in 0 until lowerPassword.length - 2) {
            val char1 = lowerPassword[i]
            val char2 = lowerPassword[i + 1]
            val char3 = lowerPassword[i + 2]
            
            if (char1.isDigit() && char2.isDigit() && char3.isDigit()) {
                if (char2 == char1 + 1 && char3 == char2 + 1) {
                    return true
                }
                if (char2 == char1 - 1 && char3 == char2 - 1) {
                    return true
                }
            }
            
            // Kiểm tra chuỗi chữ cái liên tiếp
            if (char1.isLetter() && char2.isLetter() && char3.isLetter()) {
                if (char2 == char1 + 1 && char3 == char2 + 1) {
                    return true
                }
                if (char2 == char1 - 1 && char3 == char2 - 1) {
                    return true
                }
            }
        }
        return false
    }
    
    /**
     * Tính độ mạnh của mật khẩu
     */
    private fun calculatePasswordStrength(password: String): PasswordStrength {
        var score = 0
        
        // Điểm cho độ dài
        when {
            password.length >= 12 -> score += 2
            password.length >= 8 -> score += 1
        }
        
        // Điểm cho các loại ký tự
        if (UPPERCASE_PATTERN.matcher(password).find()) score += 1
        if (LOWERCASE_PATTERN.matcher(password).find()) score += 1
        if (DIGIT_PATTERN.matcher(password).find()) score += 1
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) score += 1
        
        // Điểm cho độ phức tạp
        if (password.length >= 10) score += 1
        if (password.length >= 14) score += 1
        
        // Trừ điểm cho các vấn đề
        if (isCommonPassword(password)) score -= 2
        if (hasRepeatingCharacters(password)) score -= 1
        if (hasSequentialCharacters(password)) score -= 1
        
        return when (score) {
            in 0..2 -> PasswordStrength.WEAK
            in 3..4 -> PasswordStrength.FAIR
            in 5..6 -> PasswordStrength.GOOD
            in 7..8 -> PasswordStrength.STRONG
            else -> PasswordStrength.VERY_STRONG
        }
    }
    
    /**
     * Tính điểm số mật khẩu (0-100)
     */
    private fun calculatePasswordScore(password: String): Int {
        var score = 0
        
        // Điểm cho độ dài (0-25 điểm)
        score += minOf(25, password.length * 2)
        
        // Điểm cho các loại ký tự (0-20 điểm)
        if (UPPERCASE_PATTERN.matcher(password).find()) score += 5
        if (LOWERCASE_PATTERN.matcher(password).find()) score += 5
        if (DIGIT_PATTERN.matcher(password).find()) score += 5
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) score += 5
        
        // Điểm cho độ phức tạp (0-30 điểm)
        val uniqueChars = password.toSet().size
        score += minOf(15, uniqueChars)
        
        if (password.length >= 12) score += 10
        if (password.length >= 16) score += 5
        
        // Trừ điểm cho các vấn đề
        if (isCommonPassword(password)) score -= 20
        if (hasRepeatingCharacters(password)) score -= 10
        if (hasSequentialCharacters(password)) score -= 10
        if (password.contains(" ")) score -= 5
        
        return maxOf(0, minOf(100, score))
    }
    
    /**
     * Lấy gợi ý cải thiện mật khẩu
     */
    fun getPasswordSuggestions(): List<String> {
        return listOf(
            "Sử dụng ít nhất 8 ký tự",
            "Kết hợp chữ hoa, chữ thường, số và ký tự đặc biệt",
            "Tránh thông tin cá nhân (tên, ngày sinh, số điện thoại)",
            "Không sử dụng mật khẩu phổ biến (123456, password, qwerty)",
            "Tránh chuỗi liên tiếp (abc, 123) hoặc lặp lại (aaa, 111)",
            "Sử dụng cụm từ dễ nhớ nhưng khó đoán",
            "Ví dụ: MyDog@2024!, Coffee#Morning7, Book\$Reading9"
        )
    }
}
