package com.example.jetpackcompose.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackcompose.ui.components.CategoryBarChart
import com.example.jetpackcompose.ui.components.StatusDonutChart
import com.example.jetpackcompose.ui.viewmodel.BookViewModel

@Composable
fun StatisticsScreen(
    viewModel: BookViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.statistics.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "📊 Thống Kê Sách Cá Nhân",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Responsive Adaptive Grid for Summary Metrics
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val isWideScreen = maxWidth >= 600.dp

            if (isWideScreen) {
                // Wide layout: 3 cards per row
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "Tổng số sách",
                            value = "${stats.totalBooks}",
                            subtitle = "cuốn sách",
                            icon = Icons.Default.MenuBook,
                            iconColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Đã đọc xong",
                            value = "${stats.readCount}",
                            subtitle = "cuốn sách",
                            icon = Icons.Default.CheckCircle,
                            iconColor = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Đang đọc",
                            value = "${stats.readingCount}",
                            subtitle = "cuốn sách",
                            icon = Icons.Default.Pending,
                            iconColor = Color(0xFFFF9800),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "Tỷ lệ đọc",
                            value = "${stats.completionPercentage}%",
                            subtitle = "${stats.totalPagesRead}/${stats.totalPagesOverall} trang",
                            icon = Icons.Default.TrendingUp,
                            iconColor = Color(0xFF0288D1),
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Số trang đã đọc",
                            value = "${stats.totalPagesRead}",
                            subtitle = "trang sách",
                            icon = Icons.Default.AutoStories,
                            iconColor = Color(0xFF8E24AA),
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Đánh giá TB",
                            value = if (stats.averageRating > 0) String.format("%.1f ⭐", stats.averageRating) else "N/A",
                            subtitle = "điểm trung bình",
                            icon = Icons.Default.Star,
                            iconColor = Color(0xFFFFB300),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                // Phone Portrait layout: 2 cards per row
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "Tổng số sách",
                            value = "${stats.totalBooks}",
                            subtitle = "cuốn sách",
                            icon = Icons.Default.MenuBook,
                            iconColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Đã đọc xong",
                            value = "${stats.readCount}",
                            subtitle = "cuốn sách",
                            icon = Icons.Default.CheckCircle,
                            iconColor = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "Đang đọc",
                            value = "${stats.readingCount}",
                            subtitle = "cuốn sách",
                            icon = Icons.Default.Pending,
                            iconColor = Color(0xFFFF9800),
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Tỷ lệ đọc",
                            value = "${stats.completionPercentage}%",
                            subtitle = "${stats.totalPagesRead}/${stats.totalPagesOverall} trang",
                            icon = Icons.Default.TrendingUp,
                            iconColor = Color(0xFF0288D1),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "Số trang đã đọc",
                            value = "${stats.totalPagesRead}",
                            subtitle = "trang sách",
                            icon = Icons.Default.AutoStories,
                            iconColor = Color(0xFF8E24AA),
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Đánh giá TB",
                            value = if (stats.averageRating > 0) String.format("%.1f ⭐", stats.averageRating) else "N/A",
                            subtitle = "điểm trung bình",
                            icon = Icons.Default.Star,
                            iconColor = Color(0xFFFFB300),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Donut Chart for Statuses & Bar Chart for Categories
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val isTabletOrLandscape = maxWidth >= 720.dp

            if (isTabletOrLandscape) {
                // Wide layout: Place Donut chart and Bar chart side-by-side!
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatusDonutChart(
                        statusCounts = stats.statusCounts,
                        totalBooks = stats.totalBooks,
                        modifier = Modifier.weight(1f)
                    )
                    CategoryBarChart(
                        categoryCounts = stats.categoryCounts,
                        totalBooks = stats.totalBooks,
                        modifier = Modifier.weight(1.2f)
                    )
                }
            } else {
                // Vertical stacked layout for phones
                Column {
                    StatusDonutChart(
                        statusCounts = stats.statusCounts,
                        totalBooks = stats.totalBooks
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CategoryBarChart(
                        categoryCounts = stats.categoryCounts,
                        totalBooks = stats.totalBooks
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
