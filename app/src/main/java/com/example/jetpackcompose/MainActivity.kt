package com.example.jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackcompose.data.model.User
import com.example.jetpackcompose.ui.screens.AddEditBookDialog
import com.example.jetpackcompose.ui.screens.AuthScreen
import com.example.jetpackcompose.ui.screens.BookDetailDialog
import com.example.jetpackcompose.ui.screens.BookListScreen
import com.example.jetpackcompose.ui.screens.StatisticsScreen
import com.example.jetpackcompose.ui.theme.JetpackComposeTheme
import com.example.jetpackcompose.ui.viewmodel.AuthViewModel
import com.example.jetpackcompose.ui.viewmodel.BookViewModel
import com.example.jetpackcompose.ui.viewmodel.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val bookViewModel: BookViewModel = viewModel()
            val authViewModel: AuthViewModel = viewModel()

            val themeMode by bookViewModel.themeMode.collectAsState()
            val currentUser by authViewModel.currentUser.collectAsState()

            JetpackComposeTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (currentUser == null) {
                        AuthScreen(authViewModel = authViewModel)
                    } else {
                        PersonalBookApp(
                            viewModel = bookViewModel,
                            authViewModel = authViewModel,
                            currentUser = currentUser!!
                        )
                    }
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector
) {
    LIBRARY("Tủ sách", Icons.Default.MenuBook),
    STATISTICS("Thống kê", Icons.Default.BarChart),
    FAVORITES("Yêu thích", Icons.Default.Favorite)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalBookApp(
    viewModel: BookViewModel = viewModel(),
    authViewModel: AuthViewModel,
    currentUser: User
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.LIBRARY) }
    var showThemeMenu by remember { mutableStateOf(false) }
    var showUserMenu by remember { mutableStateOf(false) }

    val themeMode by viewModel.themeMode.collectAsState()
    val detailBook = viewModel.selectedBookForDetail
    val editingBook = viewModel.selectedBookForEdit
    val isAddEditOpen = viewModel.isAddingNewBook || editingBook != null

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.label
                        )
                    },
                    label = { Text(destination.label) },
                    selected = destination == currentDestination,
                    onClick = {
                        currentDestination = destination
                        if (destination == AppDestinations.FAVORITES) {
                            viewModel.setShowFavoritesOnly(true)
                        } else if (destination == AppDestinations.LIBRARY) {
                            viewModel.setShowFavoritesOnly(false)
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "📚 Quản Lý Sách Cá Nhân",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "👋 Chào ${currentUser.fullName}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    actions = {
                        // User Profile & Logout Menu
                        Box {
                            IconButton(onClick = { showUserMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Tài khoản",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            DropdownMenu(
                                expanded = showUserMenu,
                                onDismissRequest = { showUserMenu = false }
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                    Text(
                                        text = currentUser.fullName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = currentUser.email,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                HorizontalDivider()

                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.ExitToApp,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Đăng xuất",
                                                color = MaterialTheme.colorScheme.error,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    },
                                    onClick = {
                                        showUserMenu = false
                                        authViewModel.logout()
                                    }
                                )
                            }
                        }

                        // Theme Mode Switcher Menu
                        Box {
                            IconButton(onClick = { showThemeMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.Brightness4,
                                    contentDescription = "Chuyển giao diện"
                                )
                            }

                            DropdownMenu(
                                expanded = showThemeMenu,
                                onDismissRequest = { showThemeMenu = false }
                            ) {
                                ThemeMode.entries.forEach { mode ->
                                    val label = when (mode) {
                                        ThemeMode.EPAPER_LIGHT -> "📖 Giấy sách E-Paper (Sáng)"
                                        ThemeMode.DARK -> "🌙 Chế độ Tối (Ban đêm)"
                                        ThemeMode.SYSTEM -> "🖥️ Theo Hệ thống"
                                    }
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = label,
                                                fontWeight = if (themeMode == mode) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            viewModel.setThemeMode(mode)
                                            showThemeMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            val modifier = Modifier.padding(innerPadding)

            when (currentDestination) {
                AppDestinations.LIBRARY, AppDestinations.FAVORITES -> {
                    BookListScreen(
                        viewModel = viewModel,
                        modifier = modifier
                    )
                }
                AppDestinations.STATISTICS -> {
                    StatisticsScreen(
                        viewModel = viewModel,
                        modifier = modifier
                    )
                }
            }

            // Book Detail Dialog
            detailBook?.let { book ->
                BookDetailDialog(
                    book = book,
                    onDismiss = { viewModel.closeDialogs() },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onUpdateProgress = { b, pages -> viewModel.updateReadingProgress(b, pages) },
                    onEdit = { viewModel.openEditBook(it) },
                    onDelete = { viewModel.deleteBook(it) }
                )
            }

            // Add / Edit Book Dialog
            if (isAddEditOpen) {
                AddEditBookDialog(
                    bookToEdit = editingBook,
                    onDismiss = { viewModel.closeDialogs() },
                    onSave = { title, author, category, coverUrl, status, rating, totalPages, readPages, notes ->
                        viewModel.saveBook(
                            title,
                            author,
                            category,
                            coverUrl,
                            status,
                            rating,
                            totalPages,
                            readPages,
                            notes
                        )
                    }
                )
            }
        }
    }
}
