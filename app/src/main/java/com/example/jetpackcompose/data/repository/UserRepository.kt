package com.example.jetpackcompose.data.repository

import com.example.jetpackcompose.data.local.UserDao
import com.example.jetpackcompose.data.model.User

class UserRepository(private val userDao: UserDao) {

    suspend fun login(email: String, password: String): Result<User> {
        val user = userDao.login(email.trim().lowercase(), password)
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("Email hoặc mật khẩu không chính xác."))
        }
    }

    suspend fun register(fullName: String, email: String, password: String): Result<User> {
        val cleanEmail = email.trim().lowercase()
        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return Result.failure(Exception("Email này đã được đăng ký tài khoản."))
        }

        val newUser = User(
            email = cleanEmail,
            passwordHash = password,
            fullName = fullName.trim()
        )
        val id = userDao.registerUser(newUser)
        return Result.success(newUser.copy(id = id.toInt()))
    }
}
