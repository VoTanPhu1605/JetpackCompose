package com.example.jetpackcompose.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Custom Interactive Scrollbar Modifier for LazyVerticalGrid.
 * Allows dragging the scrollbar thumb and tapping the track to scroll immediately.
 */
@Composable
fun Modifier.lazyGridScrollbar(
    state: LazyGridState,
    width: Dp = 8.dp,
    color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
    trackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    cornerRadius: Dp = 4.dp
): Modifier {
    val coroutineScope = rememberCoroutineScope()
    var isDragging by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (state.isScrollInProgress || isDragging) 1f else 0.5f,
        animationSpec = tween(durationMillis = 300),
        label = "grid_scrollbar_alpha"
    )

    return this
        .pointerInput(state) {
            detectTapGestures { offset ->
                val totalItems = state.layoutInfo.totalItemsCount
                if (totalItems > 0 && offset.x >= size.width - 36.dp.toPx()) {
                    val targetProgress = (offset.y / size.height).coerceIn(0f, 1f)
                    val targetIndex = (targetProgress * totalItems).toInt().coerceIn(0, totalItems - 1)
                    coroutineScope.launch {
                        state.scrollToItem(targetIndex)
                    }
                }
            }
        }
        .pointerInput(state) {
            detectVerticalDragGestures(
                onDragStart = { offset ->
                    if (offset.x >= size.width - 36.dp.toPx()) {
                        isDragging = true
                    }
                },
                onDragEnd = { isDragging = false },
                onDragCancel = { isDragging = false },
                onVerticalDrag = { change, _ ->
                    if (isDragging) {
                        change.consume()
                        val totalItems = state.layoutInfo.totalItemsCount
                        if (totalItems > 0) {
                            val targetProgress = (change.position.y / size.height).coerceIn(0f, 1f)
                            val targetIndex = (targetProgress * totalItems).toInt().coerceIn(0, totalItems - 1)
                            coroutineScope.launch {
                                state.scrollToItem(targetIndex)
                            }
                        }
                    }
                }
            )
        }
        .drawWithContent {
            drawContent()

            val firstVisibleIndex = state.firstVisibleItemIndex
            val visibleItemsInfo = state.layoutInfo.visibleItemsInfo
            val totalItemsCount = state.layoutInfo.totalItemsCount

            if (totalItemsCount > 0 && visibleItemsInfo.isNotEmpty()) {
                val viewportHeight = size.height
                val visibleCount = visibleItemsInfo.size.toFloat()

                if (visibleCount < totalItemsCount) {
                    val indicatorHeight = (viewportHeight * (visibleCount / totalItemsCount)).coerceAtLeast(40.dp.toPx())
                    val scrollProgress = firstVisibleIndex.toFloat() / (totalItemsCount - visibleCount).coerceAtLeast(1f)
                    val indicatorOffset = (scrollProgress * (viewportHeight - indicatorHeight)).coerceIn(0f, viewportHeight - indicatorHeight)

                    val widthPx = width.toPx()
                    val cornerRadiusPx = cornerRadius.toPx()
                    val xOffset = size.width - widthPx - 2.dp.toPx()

                    // Scrollbar Track
                    drawRoundRect(
                        color = trackColor,
                        topLeft = Offset(xOffset, 0f),
                        size = Size(widthPx, viewportHeight),
                        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                    )

                    // Scrollbar Thumb
                    drawRoundRect(
                        color = color.copy(alpha = color.alpha * alpha),
                        topLeft = Offset(xOffset, indicatorOffset),
                        size = Size(widthPx, indicatorHeight),
                        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                    )
                }
            }
        }
}

/**
 * Custom Interactive Scrollbar Modifier for Column/Row verticalScroll(scrollState).
 * Allows dragging the scrollbar thumb and tapping the track to scroll immediately.
 */
@Composable
fun Modifier.verticalScrollbar(
    scrollState: ScrollState,
    width: Dp = 8.dp,
    color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
    trackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    cornerRadius: Dp = 4.dp
): Modifier {
    val coroutineScope = rememberCoroutineScope()
    var isDragging by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (scrollState.isScrollInProgress || isDragging) 1f else 0.5f,
        animationSpec = tween(durationMillis = 300),
        label = "scrollbar_alpha"
    )

    return this
        .pointerInput(scrollState) {
            detectTapGestures { offset ->
                if (scrollState.maxValue > 0 && offset.x >= size.width - 36.dp.toPx()) {
                    val targetProgress = (offset.y / size.height).coerceIn(0f, 1f)
                    val targetScroll = (targetProgress * scrollState.maxValue).toInt()
                    coroutineScope.launch {
                        scrollState.scrollTo(targetScroll)
                    }
                }
            }
        }
        .pointerInput(scrollState) {
            detectVerticalDragGestures(
                onDragStart = { offset ->
                    if (offset.x >= size.width - 36.dp.toPx()) {
                        isDragging = true
                    }
                },
                onDragEnd = { isDragging = false },
                onDragCancel = { isDragging = false },
                onVerticalDrag = { change, _ ->
                    if (isDragging && scrollState.maxValue > 0) {
                        change.consume()
                        val targetProgress = (change.position.y / size.height).coerceIn(0f, 1f)
                        val targetScroll = (targetProgress * scrollState.maxValue).toInt()
                        coroutineScope.launch {
                            scrollState.scrollTo(targetScroll)
                        }
                    }
                }
            )
        }
        .drawWithContent {
            drawContent()

            val viewportHeight = size.height
            val maxScroll = scrollState.maxValue.toFloat()

            if (maxScroll > 0) {
                val totalContentHeight = maxScroll + viewportHeight
                val indicatorHeight = (viewportHeight / totalContentHeight * viewportHeight).coerceAtLeast(40.dp.toPx())
                val scrollProgress = scrollState.value.toFloat() / maxScroll
                val indicatorOffset = scrollProgress * (viewportHeight - indicatorHeight)

                val widthPx = width.toPx()
                val cornerRadiusPx = cornerRadius.toPx()
                val xOffset = size.width - widthPx - 2.dp.toPx()

                // Scrollbar Track
                drawRoundRect(
                    color = trackColor,
                    topLeft = Offset(xOffset, 0f),
                    size = Size(widthPx, viewportHeight),
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )

                // Scrollbar Thumb
                drawRoundRect(
                    color = color.copy(alpha = color.alpha * alpha),
                    topLeft = Offset(xOffset, indicatorOffset),
                    size = Size(widthPx, indicatorHeight),
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
            }
        }
}
