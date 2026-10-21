package com.example.jetpackcompose.data.repository

import com.example.jetpackcompose.data.local.BookDao
import com.example.jetpackcompose.data.model.Book
import kotlinx.coroutines.flow.Flow

class BookRepository(private val bookDao: BookDao) {

    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()
    val favoriteBooks: Flow<List<Book>> = bookDao.getFavoriteBooks()

    fun searchBooks(query: String): Flow<List<Book>> {
        return if (query.isBlank()) {
            bookDao.getAllBooks()
        } else {
            bookDao.searchBooks(query)
        }
    }

    fun getBookById(id: Int): Flow<Book?> = bookDao.getBookById(id)

    suspend fun insertBook(book: Book): Long = bookDao.insertBook(book)

    suspend fun updateBook(book: Book) = bookDao.updateBook(book)

    suspend fun deleteBook(book: Book) = bookDao.deleteBook(book)

    suspend fun deleteBookById(id: Int) = bookDao.deleteBookById(id)
}
