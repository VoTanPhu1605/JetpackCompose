package com.example.jetpackcompose.data.model

enum class Category(val displayName: String, val colorHex: Long) {
    NOVEL("Tiểu thuyết", 0xFF64B5F6),       // Light Blue
    SKILLS("Kỹ năng sống", 0xFF4DB6AC),     // Teal
    SCIENCE("Khoa học", 0xFFBA68C8),        // Purple
    HISTORY("Lịch sử", 0xFFFF8A65),        // Coral
    ECONOMICS("Kinh tế", 0xFFFFD54F),      // Yellow
    PSYCHOLOGY("Tâm lý học", 0xFFA1887F),   // Brown
    PHILOSOPHY("Triết học", 0xFF81C784),   // Green
    COMIC("Truyện tranh", 0xFF4DD0E1),      // Cyan
    OTHER("Khác", 0xFF90A4AE);             // Blue Grey

    companion object {
        fun getAllDisplayNames(): List<String> = entries.map { it.displayName }

        fun fromDisplayName(name: String): Category {
            return entries.find { it.displayName.equals(name, ignoreCase = true) } ?: OTHER
        }
    }
}
