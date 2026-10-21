package com.example.jetpackcompose.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.jetpackcompose.data.model.Book
import com.example.jetpackcompose.data.model.Category
import com.example.jetpackcompose.data.model.ReadingStatus
import com.example.jetpackcompose.ui.components.BookCoverImage
import com.example.jetpackcompose.ui.components.CategoryChip
import com.example.jetpackcompose.ui.components.StarRating

@Composable
fun AddEditBookDialog(
    bookToEdit: Book?,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        author: String,
        category: String,
        coverUrl: String,
        status: ReadingStatus,
        rating: Float,
        totalPages: Int,
        readPages: Int,
        notes: String
    ) -> Unit
) {
    var title by remember { mutableStateOf(bookToEdit?.title ?: "") }
    var author by remember { mutableStateOf(bookToEdit?.author ?: "") }
    var coverUrl by remember { mutableStateOf(bookToEdit?.coverUrl ?: "") }
    var selectedCategory by remember { mutableStateOf(bookToEdit?.category ?: Category.NOVEL.displayName) }
    var selectedStatus by remember { mutableStateOf(bookToEdit?.statusEnum ?: ReadingStatus.UNREAD) }
    var rating by remember { mutableFloatStateOf(bookToEdit?.rating ?: 0f) }
    var totalPagesText by remember { mutableStateOf(bookToEdit?.totalPages?.takeIf { it > 0 }?.toString() ?: "") }
    var readPagesText by remember { mutableStateOf(bookToEdit?.readPages?.takeIf { it > 0 }?.toString() ?: "") }
    var notes by remember { mutableStateOf(bookToEdit?.notes ?: "") }

    var titleError by remember { mutableStateOf(false) }
    var authorError by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (bookToEdit == null) "➕ Thêm sách mới" else "✏️ Sửa thông tin sách",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) titleError = false
                    },
                    label = { Text("Tên sách *") },
                    isError = titleError,
                    supportingText = if (titleError) { { Text("Vui lòng nhập tên sách") } } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Author Input
                OutlinedTextField(
                    value = author,
                    onValueChange = {
                        author = it
                        if (it.isNotBlank()) authorError = false
                    },
                    label = { Text("Tác giả *") },
                    isError = authorError,
                    supportingText = if (authorError) { { Text("Vui lòng nhập tên tác giả") } } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Cover Image URL Input & Preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = coverUrl,
                        onValueChange = { coverUrl = it },
                        label = { Text("URL Ảnh bìa (Tùy chọn)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    BookCoverImage(
                        coverUrl = coverUrl,
                        modifier = Modifier
                            .width(50.dp)
                            .height(70.dp),
                        cornerRadius = 8.dp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Selection
                Text(
                    text = "📚 Thể loại",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Category.entries.forEach { cat ->
                        CategoryChip(
                            categoryName = cat.displayName,
                            isSelected = selectedCategory == cat.displayName,
                            onClick = { selectedCategory = cat.displayName }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status Selection
                Text(
                    text = "📖 Trạng thái đọc",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReadingStatus.entries.forEach { status ->
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status },
                            label = { Text(status.displayName, maxLines = 1) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rating
                Text(
                    text = "⭐ Đánh giá",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                StarRating(
                    rating = rating,
                    starSize = 32.dp,
                    isInteractive = true,
                    onRatingChanged = { rating = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Page count fields
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = totalPagesText,
                        onValueChange = { totalPagesText = it.filter { c -> c.isDigit() } },
                        label = { Text("Tổng số trang") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = readPagesText,
                        onValueChange = { readPagesText = it.filter { c -> c.isDigit() } },
                        label = { Text("Đã đọc (trang)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notes Field
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Ghi chú / Trích dẫn hay") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                    ) {
                        Text("Hủy", maxLines = 1)
                    }

                    Button(
                        onClick = {
                            if (title.isBlank()) titleError = true
                            if (author.isBlank()) authorError = true

                            if (title.isNotBlank() && author.isNotBlank()) {
                                val totalPages = totalPagesText.toIntOrNull() ?: 0
                                val readPages = readPagesText.toIntOrNull() ?: 0
                                onSave(
                                    title,
                                    author,
                                    selectedCategory,
                                    coverUrl,
                                    selectedStatus,
                                    rating,
                                    totalPages,
                                    readPages,
                                    notes
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = if (bookToEdit == null) "Thêm sách" else "Lưu thay đổi",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
