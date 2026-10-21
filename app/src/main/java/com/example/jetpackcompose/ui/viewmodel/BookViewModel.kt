package com.example.jetpackcompose.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.local.AppDatabase
import com.example.jetpackcompose.data.model.Book
import com.example.jetpackcompose.data.model.ReadingStatus
import com.example.jetpackcompose.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ThemeMode {
    EPAPER_LIGHT,
    DARK,
    SYSTEM
}

enum class SortOption(val label: String) {
    NEWEST("Mới nhất"),
    TITLE("Tên A-Z"),
    AUTHOR("Tác giả A-Z"),
    RATING("Đánh giá cao nhất"),
    PROGRESS("Tiến độ đọc")
}

data class BookStatistics(
    val totalBooks: Int = 0,
    val readCount: Int = 0,
    val readingCount: Int = 0,
    val unreadCount: Int = 0,
    val totalPagesRead: Int = 0,
    val totalPagesOverall: Int = 0,
    val averageRating: Float = 0f,
    val categoryCounts: Map<String, Int> = emptyMap(),
    val statusCounts: Map<ReadingStatus, Int> = emptyMap()
) {
    val completionPercentage: Int
        get() = if (totalPagesOverall > 0) ((totalPagesRead.toFloat() / totalPagesOverall.toFloat()) * 100).toInt() else 0
}

class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookRepository

    init {
        val bookDao = AppDatabase.getDatabase(application).bookDao()
        repository = BookRepository(bookDao)
        viewModelScope.launch {
            AppDatabase.populateDatabase(bookDao, forceRefresh = true)
        }
    }

    // Theme Mode
    private val _themeMode = MutableStateFlow(ThemeMode.EPAPER_LIGHT)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    // Filter & Search states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<ReadingStatus?>(null)
    val selectedStatusFilter: StateFlow<ReadingStatus?> = _selectedStatusFilter.asStateFlow()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    private val _sortBy = MutableStateFlow(SortOption.NEWEST)
    val sortBy: StateFlow<SortOption> = _sortBy.asStateFlow()

    // Dialog state
    var selectedBookForDetail by mutableStateOf<Book?>(null)
        private set

    var selectedBookForEdit by mutableStateOf<Book?>(null)
        private set

    var isAddingNewBook by mutableStateOf(false)
        private set

    // All Books Flow from Room
    val allBooks: StateFlow<List<Book>> = repository.allBooks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filtered & Sorted Books Flow
    val filteredBooks: StateFlow<List<Book>> = combine(
        allBooks,
        searchQuery,
        selectedCategoryFilter,
        selectedStatusFilter,
        showFavoritesOnly,
        sortBy
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val books = args[0] as List<Book>
        val query = args[1] as String
        val cat = args[2] as String?
        val status = args[3] as ReadingStatus?
        val favOnly = args[4] as Boolean
        val sort = args[5] as SortOption

        books.filter { book ->
            val matchesQuery = query.isBlank() ||
                    book.title.contains(query, ignoreCase = true) ||
                    book.author.contains(query, ignoreCase = true)
            val matchesCategory = cat == null || book.category.equals(cat, ignoreCase = true)
            val matchesStatus = status == null || book.statusEnum == status
            val matchesFav = !favOnly || book.isFavorite

            matchesQuery && matchesCategory && matchesStatus && matchesFav
        }.let { list ->
            when (sort) {
                SortOption.NEWEST -> list.sortedByDescending { it.updatedAt }
                SortOption.TITLE -> list.sortedBy { it.title.lowercase() }
                SortOption.AUTHOR -> list.sortedBy { it.author.lowercase() }
                SortOption.RATING -> list.sortedByDescending { it.rating }
                SortOption.PROGRESS -> list.sortedByDescending { it.progressPercentage }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Statistics Flow
    val statistics: StateFlow<BookStatistics> = combine(allBooks) { booksArray ->
        val books = booksArray[0]
        if (books.isEmpty()) {
            BookStatistics()
        } else {
            val total = books.size
            val read = books.count { it.statusEnum == ReadingStatus.READ }
            val reading = books.count { it.statusEnum == ReadingStatus.READING }
            val unread = books.count { it.statusEnum == ReadingStatus.UNREAD }

            val pagesRead = books.sumOf { it.readPages }
            val pagesTotal = books.sumOf { it.totalPages }

            val ratedBooks = books.filter { it.rating > 0 }
            val avgRating = if (ratedBooks.isNotEmpty()) {
                ratedBooks.map { it.rating }.average().toFloat()
            } else 0f

            val catCounts = books.groupBy { it.category }.mapValues { it.value.size }
            val statusCounts = books.groupBy { it.statusEnum }.mapValues { it.value.size }

            BookStatistics(
                totalBooks = total,
                readCount = read,
                readingCount = reading,
                unreadCount = unread,
                totalPagesRead = pagesRead,
                totalPagesOverall = pagesTotal,
                averageRating = avgRating,
                categoryCounts = catCounts,
                statusCounts = statusCounts
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BookStatistics()
    )

    // User Intent Handlers
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategoryFilter(category: String?) {
        _selectedCategoryFilter.value = category
    }

    fun setSelectedStatusFilter(status: ReadingStatus?) {
        _selectedStatusFilter.value = status
    }

    fun setShowFavoritesOnly(showOnly: Boolean) {
        _showFavoritesOnly.value = showOnly
    }

    fun setSortBy(option: SortOption) {
        _sortBy.value = option
    }

    fun toggleFavorite(book: Book) {
        viewModelScope.launch {
            repository.updateBook(
                book.copy(
                    isFavorite = !book.isFavorite,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateReadingProgress(book: Book, readPages: Int) {
        viewModelScope.launch {
            val clampedPages = readPages.coerceIn(0, book.totalPages)
            val newStatus = when {
                clampedPages >= book.totalPages && book.totalPages > 0 -> ReadingStatus.READ.name
                clampedPages > 0 -> ReadingStatus.READING.name
                else -> ReadingStatus.UNREAD.name
            }
            repository.updateBook(
                book.copy(
                    readPages = clampedPages,
                    readingStatus = newStatus,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun saveBook(
        title: String,
        author: String,
        category: String,
        coverUrl: String,
        status: ReadingStatus,
        rating: Float,
        totalPages: Int,
        readPages: Int,
        notes: String
    ) {
        viewModelScope.launch {
            val clampedReadPages = readPages.coerceIn(0, totalPages)
            val bookToSave = selectedBookForEdit?.copy(
                title = title,
                author = author,
                category = category,
                coverUrl = coverUrl,
                readingStatus = status.name,
                rating = rating,
                totalPages = totalPages,
                readPages = clampedReadPages,
                notes = notes,
                updatedAt = System.currentTimeMillis()
            ) ?: Book(
                title = title,
                author = author,
                category = category,
                coverUrl = coverUrl,
                readingStatus = status.name,
                rating = rating,
                totalPages = totalPages,
                readPages = clampedReadPages,
                notes = notes,
                updatedAt = System.currentTimeMillis()
            )

            if (bookToSave.id == 0) {
                repository.insertBook(bookToSave)
            } else {
                repository.updateBook(bookToSave)
            }
            closeDialogs()
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book)
            closeDialogs()
        }
    }

    // Dialog Trigger Methods
    fun openDetail(book: Book) {
        selectedBookForDetail = book
    }

    fun openAddBook() {
        isAddingNewBook = true
    }

    fun openEditBook(book: Book) {
        selectedBookForEdit = book
        selectedBookForDetail = null
    }

    fun closeDialogs() {
        selectedBookForDetail = null
        selectedBookForEdit = null
        isAddingNewBook = false
    }
}
