package com.example.jetpackcompose.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StarRating(
    rating: Float,
    maxStars: Int = 5,
    starSize: Dp = 20.dp,
    starColor: Color = Color(0xFFFFB300), // Gold / Amber
    isInteractive: Boolean = false,
    onRatingChanged: ((Float) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isFull = rating >= i
            val isHalf = rating >= i - 0.5f && rating < i

            val icon = when {
                isFull -> Icons.Filled.Star
                isHalf -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Outlined.StarOutline
            }

            val iconColor = if (isFull || isHalf) starColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)

            Icon(
                imageVector = icon,
                contentDescription = "Rating star $i",
                tint = iconColor,
                modifier = Modifier
                    .size(starSize)
                    .padding(horizontal = 2.dp)
                    .then(
                        if (isInteractive && onRatingChanged != null) {
                            Modifier.clickable { onRatingChanged(i.toFloat()) }
                        } else Modifier
                    )
            )
        }
    }
}
