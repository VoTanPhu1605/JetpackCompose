package com.example.jetpackcompose.data.model

import androidx.compose.ui.graphics.Color

enum class ReadingStatus(
    val displayName: String,
    val color: Long // ARGB Color for Compose UI
) {
    UNREAD("Chưa đọc", 0xFFE57373),  // Soft Red
    READING("Đang đọc", 0xFFFFB74D), // Soft Orange/Amber
    READ("Đã đọc", 0xFF81C784);     // Soft Green

    companion object {
        fun fromString(value: String): ReadingStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true) }
                ?: UNREAD
        }
    }
}
