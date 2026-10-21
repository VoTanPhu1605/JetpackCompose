package com.example.jetpackcompose.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.jetpackcompose.data.model.Book
import com.example.jetpackcompose.data.model.Category
import com.example.jetpackcompose.data.model.ReadingStatus
import com.example.jetpackcompose.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Book::class, User::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "personal_books_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.bookDao(), forceRefresh = true)
                        populateDefaultUser(database.userDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.bookDao(), forceRefresh = false)
                        populateDefaultUser(database.userDao())
                    }
                }
            }
        }

        suspend fun populateDefaultUser(userDao: UserDao) {
            if (userDao.getUserCount() == 0) {
                userDao.registerUser(
                    User(
                        email = "demo@gmail.com",
                        passwordHash = "123456",
                        fullName = "Nguyễn Văn Đọc Sách"
                    )
                )
            }
        }

        suspend fun populateDatabase(bookDao: BookDao, forceRefresh: Boolean = false) {
            if (forceRefresh || bookDao.getBookCount() < 15) {
                bookDao.deleteAllBooks()
                val sampleBooks = listOf(
                    Book(
                        title = "Nhà Giả Kim",
                        author = "Paulo Coelho",
                        category = Category.NOVEL.displayName,
                        coverUrl = "https://covers.openlibrary.org/b/id/12818862-L.jpg",
                        readingStatus = ReadingStatus.READ.name,
                        rating = 5.0f,
                        totalPages = 225,
                        readPages = 225,
                        notes = "Cuốn sách truyền cảm hứng tuyệt vời về việc theo đuổi ước mơ.",
                        isFavorite = true
                    ),
                    Book(
                        title = "Đắc Nhân Tâm",
                        author = "Dale Carnegie",
                        category = Category.SKILLS.displayName,
                        coverUrl = "https://covers.openlibrary.org/b/id/12520371-L.jpg",
                        readingStatus = ReadingStatus.READ.name,
                        rating = 5.0f,
                        totalPages = 320,
                        readPages = 320,
                        notes = "Nghệ thuật ứng xử và thu phục lòng người.",
                        isFavorite = true
                    ),
                    Book(
                        title = "Sapiens: Lược Sử Loài Người",
                        author = "Yuval Noah Harari",
                        category = Category.HISTORY.displayName,
                        coverUrl = "https://covers.openlibrary.org/b/id/14539828-L.jpg",
                        readingStatus = ReadingStatus.READING.name,
                        rating = 4.8f,
                        totalPages = 560,
                        readPages = 310,
                        notes = "Góc nhìn sâu sắc về lịch sử tiến hóa của loài người.",
                        isFavorite = false
                    ),
                    Book(
                        title = "Tư Duy Nhanh Và Chậm",
                        author = "Daniel Kahneman",
                        category = Category.PSYCHOLOGY.displayName,
                        coverUrl = "https://covers.openlibrary.org/b/id/12836263-L.jpg",
                        readingStatus = ReadingStatus.READING.name,
                        rating = 4.0f,
                        totalPages = 610,
                        readPages = 150,
                        notes = "Phân tích 2 hệ thống tư duy trong não bộ con người.",
                        isFavorite = false
                    ),
                    Book(
                        title = "Doraemon - Tập 1",
                        author = "Fujiko F. Fujio",
                        category = Category.COMIC.displayName,
                        coverUrl = "https://upload.wikimedia.org/wikipedia/vi/c/c8/Doraemon_volume_1_cover.jpg",
                        readingStatus = ReadingStatus.READ.name,
                        rating = 5.0f,
                        totalPages = 190,
                        readPages = 190,
                        notes = "Truyện tranh tuổi thơ gắn liền với mèo máy thông minh.",
                        isFavorite = true
                    ),
                    Book(
                        title = "Cha Giàu Cha Nghèo",
                        author = "Robert Kiyosaki",
                        category = Category.ECONOMICS.displayName,
                        coverUrl = "https://covers.openlibrary.org/b/id/12852156-L.jpg",
                        readingStatus = ReadingStatus.READ.name,
                        rating = 4.5f,
                        totalPages = 336,
                        readPages = 336,
                        notes = "Bài học về tư duy tài chính cá nhân và đầu tư.",
                        isFavorite = false
                    ),
                    Book(
                        title = "Vũ Trụ (Cosmos)",
                        author = "Carl Sagan",
                        category = Category.SCIENCE.displayName,
                        coverUrl = "https://covers.openlibrary.org/b/id/8381831-L.jpg",
                        readingStatus = ReadingStatus.READING.name,
                        rating = 4.7f,
                        totalPages = 480,
                        readPages = 200,
                        notes = "Khám phá bí ẩn không gian và hành tinh.",
                        isFavorite = true
                    ),
                    Book(
                        title = "Tuổi Trẻ Đáng Giá Bao Nhiêu?",
                        author = "Rosie Nguyễn",
                        category = Category.SKILLS.displayName,
                        coverUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600&auto=format&fit=crop",
                        readingStatus = ReadingStatus.READ.name,
                        rating = 4.5f,
                        totalPages = 210,
                        readPages = 210,
                        notes = "Học tập, làm việc và khám phá bản thân thời tuổi trẻ.",
                        isFavorite = false
                    ),
                    Book(
                        title = "Rừng Na Uy",
                        author = "Haruki Murakami",
                        category = Category.NOVEL.displayName,
                        coverUrl = "https://covers.openlibrary.org/b/id/10522079-L.jpg",
                        readingStatus = ReadingStatus.READ.name,
                        rating = 4.5f,
                        totalPages = 450,
                        readPages = 450,
                        notes = "Tác phẩm kinh điển về tình yêu và tuổi trẻ Nhật Bản.",
                        isFavorite = true
                    ),
                    Book(
                        title = "Mắt Biếc",
                        author = "Nguyễn Nhật Ánh",
                        category = Category.NOVEL.displayName,
                        coverUrl = "https://images.unsplash.com/photo-1512820790803-83ca734da794?w=600&auto=format&fit=crop",
                        readingStatus = ReadingStatus.READ.name,
                        rating = 5.0f,
                        totalPages = 300,
                        readPages = 300,
                        notes = "Chuyện tình cảm động làng Đo Đo.",
                        isFavorite = true
                    ),
                    Book(
                        title = "Ông Già Và Biển Cả",
                        author = "Ernest Hemingway",
                        category = Category.NOVEL.displayName,
                        coverUrl = "https://covers.openlibrary.org/b/id/12822180-L.jpg",
                        readingStatus = ReadingStatus.READ.name,
                        rating = 4.0f,
                        totalPages = 128,
                        readPages = 128,
                        notes = "Biểu tượng về ý chí kiên cường của con người.",
                        isFavorite = false
                    ),
                    Book(
                        title = "Cây Cam Ngọt Của Tôi",
                        author = "José Mauro de Vasconcelos",
                        category = Category.NOVEL.displayName,
                        coverUrl = "https://covers.openlibrary.org/b/id/8750868-L.jpg",
                        readingStatus = ReadingStatus.READ.name,
                        rating = 5.0f,
                        totalPages = 244,
                        readPages = 244,
                        notes = "Câu chuyện về chú bé Zezé giàu tình cảm.",
                        isFavorite = true
                    ),
                    Book(
                        title = "Muôn Kiếp Nhân Sinh",
                        author = "Nguyên Phong",
                        category = Category.PHILOSOPHY.displayName,
                        coverUrl = "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=600&auto=format&fit=crop",
                        readingStatus = ReadingStatus.READING.name,
                        rating = 4.6f,
                        totalPages = 480,
                        readPages = 280,
                        notes = "Quy luật nhân quả và luân hồi trải qua nhiều kiếp sống.",
                        isFavorite = false
                    ),
                    Book(
                        title = "Lược Sử Thời Gian",
                        author = "Stephen Hawking",
                        category = Category.SCIENCE.displayName,
                        coverUrl = "https://covers.openlibrary.org/b/id/12553956-L.jpg",
                        readingStatus = ReadingStatus.READING.name,
                        rating = 4.5f,
                        totalPages = 256,
                        readPages = 100,
                        notes = "Từ Vụ Nổ Lớn đến Lỗ Đen.",
                        isFavorite = false
                    ),
                    Book(
                        title = "Tội Lỗi Và Hình Phạt",
                        author = "Fyodor Dostoevsky",
                        category = Category.NOVEL.displayName,
                        coverUrl = "https://covers.openlibrary.org/b/id/12589088-L.jpg",
                        readingStatus = ReadingStatus.UNREAD.name,
                        rating = 0f,
                        totalPages = 670,
                        readPages = 0,
                        notes = "Kiệt tác văn học Nga về tâm lý tội phạm.",
                        isFavorite = false
                    ),
                    Book(
                        title = "Những Người Khốn Khổ",
                        author = "Victor Hugo",
                        category = Category.NOVEL.displayName,
                        coverUrl = "https://covers.openlibrary.org/b/id/12831206-L.jpg",
                        readingStatus = ReadingStatus.UNREAD.name,
                        rating = 0f,
                        totalPages = 1200,
                        readPages = 0,
                        notes = "Bản hùng ca về tình yêu thương và nhân đạo.",
                        isFavorite = false
                    ),
                    Book(
                        title = "Không Gia Đình",
                        author = "Hector Malot",
                        category = Category.NOVEL.displayName,
                        coverUrl = "https://covers.openlibrary.org/b/id/1012338-L.jpg",
                        readingStatus = ReadingStatus.READ.name,
                        rating = 5.0f,
                        totalPages = 500,
                        readPages = 500,
                        notes = "Hành trình bôn ba kiên cường của cậu bé Rémi.",
                        isFavorite = true
                    ),
                    Book(
                        title = "Thám Tử Lừng Danh Conan - Tập 1",
                        author = "Gosho Aoyama",
                        category = Category.COMIC.displayName,
                        coverUrl = "https://upload.wikimedia.org/wikipedia/vi/3/30/Detective_Conan_Vol_1.jpg",
                        readingStatus = ReadingStatus.READ.name,
                        rating = 5.0f,
                        totalPages = 180,
                        readPages = 180,
                        notes = "Chuyến phiêu lưu phá án kỳ kịch tính của Kudo Shinichi.",
                        isFavorite = true
                    ),
                    Book(
                        title = "Đi Tìm Lẽ Sống",
                        author = "Viktor E. Frankl",
                        category = Category.PSYCHOLOGY.displayName,
                        coverUrl = "https://covers.openlibrary.org/b/id/12816990-L.jpg",
                        readingStatus = ReadingStatus.READ.name,
                        rating = 5.0f,
                        totalPages = 220,
                        readPages = 220,
                        notes = "Trải nghiệm trong trại tập trung và liệu pháp ý nghĩa.",
                        isFavorite = true
                    ),
                    Book(
                        title = "Kinh Tế Học Hài Hước",
                        author = "Steven D. Levitt",
                        category = Category.ECONOMICS.displayName,
                        coverUrl = "https://covers.openlibrary.org/b/id/12534568-L.jpg",
                        readingStatus = ReadingStatus.READ.name,
                        rating = 4.2f,
                        totalPages = 320,
                        readPages = 320,
                        notes = "Khám phá khía cạnh ẩn sau các hiện tượng đời sống.",
                        isFavorite = false
                    ),
                    Book(
                        title = "Hành Trình Về Phương Đông",
                        author = "Baird T. Spalding",
                        category = Category.PHILOSOPHY.displayName,
                        coverUrl = "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=600&auto=format&fit=crop",
                        readingStatus = ReadingStatus.READ.name,
                        rating = 4.5f,
                        totalPages = 260,
                        readPages = 260,
                        notes = "Chuyến khảo sát văn hóa tâm linh Ấn Độ.",
                        isFavorite = false
                    )
                )
                bookDao.insertBooks(sampleBooks)
            }
        }
    }
}
