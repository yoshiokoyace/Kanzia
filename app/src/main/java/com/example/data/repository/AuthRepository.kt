package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.UserEntity
import com.example.data.remote.SupabaseClient
import com.example.data.remote.model.SupabaseUserDto
import com.example.util.AppCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class AuthResult {
    data class Success(val user: UserEntity, val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(
    private val userDao: UserDao,
    context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val TAG = "AuthRepository"

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow<Boolean>(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _hasLaunchedBefore = MutableStateFlow<Boolean>(
        prefs.getBoolean(KEY_HAS_LAUNCHED, false)
    )
    val hasLaunchedBefore: StateFlow<Boolean> = _hasLaunchedBefore.asStateFlow()

    val savedUserEmail: String?
        get() = AppCache.savedEmail ?: prefs.getString(KEY_LOGGED_IN_EMAIL, null)

    init {
        instance = this
        CoroutineScope(Dispatchers.IO).launch {
            // Restore user profile from cache/DB in background for pre-filling email
            val savedEmail = savedUserEmail
            if (!savedEmail.isNullOrBlank()) {
                var user = userDao.getUserByEmail(savedEmail)
                if (user == null && SupabaseClient.isConfigured) {
                    try {
                        val remoteUsers = SupabaseClient.api?.getUsersByEmail("eq.$savedEmail")
                        if (!remoteUsers.isNullOrEmpty()) {
                            val remote = remoteUsers.first()
                            userDao.insertUser(
                                UserEntity(
                                    name = remote.name,
                                    email = remote.email,
                                    passwordHash = remote.passwordHash ?: ""
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Error checking user in Supabase: ${e.message}")
                    }
                }
                // We deliberately do NOT set _isLoggedIn.value = true here.
                // The application must always ask the user to login first before reaching the homescreen.
            }
        }
    }

    suspend fun login(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        val trimmedPass = password.trim()

        if (trimmedEmail.isEmpty() || trimmedPass.isEmpty()) {
            return@withContext AuthResult.Error("Please enter both email and password.")
        }

        var user = userDao.getUserByEmail(trimmedEmail)

        // If not in local Room DB, check Supabase
        if (user == null && SupabaseClient.isConfigured) {
            try {
                val remoteUsers = SupabaseClient.api?.getUsersByEmail("eq.$trimmedEmail")
                if (!remoteUsers.isNullOrEmpty()) {
                    val remote = remoteUsers.first()
                    userDao.insertUser(
                        UserEntity(
                            name = remote.name,
                            email = remote.email,
                            passwordHash = remote.passwordHash ?: ""
                        )
                    )
                    user = userDao.getUserByEmail(trimmedEmail)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Could not fetch user from Supabase: ${e.message}")
            }
        }

        if (user == null) {
            return@withContext AuthResult.Error("No account found with this email. Please register.")
        }

        if (user.passwordHash != trimmedPass) {
            return@withContext AuthResult.Error("Incorrect password. Try again or reset password.")
        }

        // Save session & cache email for seamless next login
        AppCache.savedEmail = user.email
        prefs.edit()
            .putString(KEY_LOGGED_IN_EMAIL, user.email)
            .putBoolean(KEY_HAS_LAUNCHED, true)
            .apply()

        _currentUser.value = user
        _isLoggedIn.value = true
        _hasLaunchedBefore.value = true

        AuthResult.Success(user, "Welcome back, ${user.name}!")
    }

    suspend fun register(name: String, email: String, password: String, confirmPass: String): AuthResult = withContext(Dispatchers.IO) {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim().lowercase()
        val trimmedPass = password.trim()

        if (trimmedName.isEmpty()) {
            return@withContext AuthResult.Error("Please enter your name.")
        }
        if (trimmedEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return@withContext AuthResult.Error("Please enter a valid email address.")
        }
        if (trimmedPass.length < 6) {
            return@withContext AuthResult.Error("Password must be at least 6 characters long.")
        }
        if (trimmedPass != confirmPass.trim()) {
            return@withContext AuthResult.Error("Passwords do not match.")
        }

        val existingUser = userDao.getUserByEmail(trimmedEmail)
        if (existingUser != null) {
            return@withContext AuthResult.Error("An account with this email already exists.")
        }

        val newUser = UserEntity(
            name = trimmedName,
            email = trimmedEmail,
            passwordHash = trimmedPass
        )
        val id = userDao.insertUser(newUser)
        val inserted = newUser.copy(id = id)

        // Sync to Supabase in background
        if (SupabaseClient.isConfigured) {
            try {
                SupabaseClient.api?.insertUser(
                    SupabaseUserDto(
                        name = trimmedName,
                        email = trimmedEmail,
                        passwordHash = trimmedPass
                    )
                )
                Log.d(TAG, "User $trimmedEmail successfully created in Supabase")
            } catch (e: Exception) {
                Log.w(TAG, "Could not insert user into Supabase: ${e.message}")
            }
        }

        // Save session & cache email for seamless next login
        AppCache.savedEmail = inserted.email
        prefs.edit()
            .putString(KEY_LOGGED_IN_EMAIL, inserted.email)
            .putBoolean(KEY_HAS_LAUNCHED, true)
            .apply()

        _currentUser.value = inserted
        _isLoggedIn.value = true
        _hasLaunchedBefore.value = true

        AuthResult.Success(inserted, "Account created successfully! Welcome, ${inserted.name}.")
    }

    suspend fun resetPassword(email: String, newPassword: String, confirmPassword: String): AuthResult = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        val trimmedNewPass = newPassword.trim()

        if (trimmedEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return@withContext AuthResult.Error("Please enter a valid email address.")
        }
        if (trimmedNewPass.length < 6) {
            return@withContext AuthResult.Error("New password must be at least 6 characters.")
        }
        if (trimmedNewPass != confirmPassword.trim()) {
            return@withContext AuthResult.Error("Passwords do not match.")
        }

        val user = userDao.getUserByEmail(trimmedEmail)
        if (user == null) {
            return@withContext AuthResult.Error("No account found with this email.")
        }

        userDao.updatePassword(trimmedEmail, trimmedNewPass)
        val updatedUser = user.copy(passwordHash = trimmedNewPass)

        // Sync update to Supabase
        if (SupabaseClient.isConfigured) {
            try {
                SupabaseClient.api?.updateUserPassword(
                    "eq.$trimmedEmail",
                    mapOf("password_hash" to trimmedNewPass)
                )
                Log.d(TAG, "Password reset synced to Supabase for $trimmedEmail")
            } catch (e: Exception) {
                Log.w(TAG, "Could not sync password reset to Supabase: ${e.message}")
            }
        }

        AuthResult.Success(updatedUser, "Password reset successfully! You can now log in.")
    }

    fun lockSession() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }

    fun markAppLaunched() {
        prefs.edit().putBoolean(KEY_HAS_LAUNCHED, true).apply()
        _hasLaunchedBefore.value = true
    }

    companion object {
        var instance: AuthRepository? = null
            private set

        private const val KEY_LOGGED_IN_EMAIL = "logged_in_user_email"
        private const val KEY_HAS_LAUNCHED = "has_launched_before"
    }
}
