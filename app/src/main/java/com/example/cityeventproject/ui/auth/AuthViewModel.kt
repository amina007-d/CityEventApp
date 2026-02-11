package com.example.cityeventproject.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityeventproject.domain.logic.Validators
import com.example.cityeventproject.domain.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : ViewModel() {

    val user: StateFlow<com.example.cityeventproject.domain.model.UserProfile?> =
        authRepo.currentUser.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val isSignedIn: Boolean get() = user.value != null

    fun signIn(email: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val emailOk = Validators.isValidEmail(email)
            val passErr = Validators.validatePassword(password)
            if (!emailOk) return@launch onResult("Invalid email format.")
            if (passErr != null) return@launch onResult(passErr)

            try {
                authRepo.signIn(email.trim(), password.trim())
                onResult(null)
            } catch (t: Throwable) {
                onResult(t.message ?: "Sign in failed")
            }
        }
    }

    fun signUp(email: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val emailOk = Validators.isValidEmail(email)
            val passErr = Validators.validatePassword(password)
            if (!emailOk) return@launch onResult("Invalid email format.")
            if (passErr != null) return@launch onResult(passErr)

            try {
                authRepo.signUp(email.trim(), password.trim())
                onResult(null)
            } catch (t: Throwable) {
                onResult(t.message ?: "Sign up failed")
            }
        }
    }

    fun signOut() = authRepo.signOut()
}
