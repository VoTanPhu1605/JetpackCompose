package com.example.jetpackcompose.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.data.local.AppDatabase
import com.example.jetpackcompose.data.model.User
import com.example.jetpackcompose.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        userRepository = UserRepository(userDao)
    }

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Vui lòng nhập đầy đủ Email và Mật khẩu."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = userRepository.login(email, password)
            _isLoading.value = false

            result.onSuccess { user ->
                _currentUser.value = user
                _successMessage.value = "Đăng nhập thành công! Chào mừng ${user.fullName}."
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: "Đăng nhập thất bại."
            }
        }
    }

    fun register(fullName: String, email: String, password: String, confirmPass: String) {
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Vui lòng nhập đầy đủ tất cả thông tin."
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            _errorMessage.value = "Địa chỉ Email không đúng định dạng."
            return
        }

        if (password.length < 6) {
            _errorMessage.value = "Mật khẩu phải có ít nhất 6 ký tự."
            return
        }

        if (password != confirmPass) {
            _errorMessage.value = "Mật khẩu xác nhận không trùng khớp."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = userRepository.register(fullName, email, password)
            _isLoading.value = false

            result.onSuccess { user ->
                _currentUser.value = user
                _successMessage.value = "Đăng ký tài khoản thành công! Tự động đăng nhập..."
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: "Đăng ký thất bại."
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        clearMessages()
    }
}
