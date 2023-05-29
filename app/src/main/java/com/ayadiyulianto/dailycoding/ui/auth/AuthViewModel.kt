package com.ayadiyulianto.dailycoding.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ayadiyulianto.dailycoding.data.AppPreferences
import com.ayadiyulianto.dailycoding.data.AuthRepository
import com.ayadiyulianto.dailycoding.data.local.entity.User
import kotlinx.coroutines.launch

class AuthViewModel(
    private val pref: AppPreferences,
    private val authRepository: AuthRepository
) : ViewModel() {

    fun isLoggedIn(): LiveData<Boolean> = pref.isLoggedIn().asLiveData()

    val userToken = pref.getAuthToken().asLiveData()

    fun saveUser(user: User) {
        viewModelScope.launch {
            pref.saveAuthUser(user)
        }
    }

    fun login(email: String, password: String) = authRepository.login(email, password)

    fun register(name: String, email: String, password: String) =
        authRepository.register(name, email, password)

    fun logout() {
        saveUser(User("", "Guest", ""))
    }
}
