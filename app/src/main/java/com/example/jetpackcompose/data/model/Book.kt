package com.example.jetpackcompose.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val author: String,
    val category: String, // Matches Category.displayName
    val coverUrl: String = "", // URL / Uri / Preset for book cover image
    val readingStatus: String = ReadingStatus.UNREAD.name, // ReadingStatus.name
    val rating: Float = 0f, // 1.0 to 5.0
    val totalPages: Int = 0,
    val readPages: Int = 0,
    val notes: String = "",
    val isFavorite: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val statusEnum: ReadingStatus
        get() = ReadingStatus.fromString(readingStatus)

    val progressPercentage: Float
        get() = if (totalPages > 0) (readPages.toFloat() / totalPages.toFloat()).coerceIn(0f, 1f) else 0f
}
