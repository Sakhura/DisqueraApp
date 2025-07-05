package com.fcterryamigos.disqueraapp.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class UserPreferences(context: Context) {

    companion object {
        private const val PREFS_FILE_NAME = "disquera_user_prefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_ADMIN = "is_admin"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_LAST_LOGIN = "last_login"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var userId: Long
        get() = sharedPreferences.getLong(KEY_USER_ID, -1L)
        set(value) = sharedPreferences.edit().putLong(KEY_USER_ID, value).apply()

    var userEmail: String?
        get() = sharedPreferences.getString(KEY_USER_EMAIL, null)
        set(value) = sharedPreferences.edit().putString(KEY_USER_EMAIL, value).apply()

    var userName: String?
        get() = sharedPreferences.getString(KEY_USER_NAME, null)
        set(value) = sharedPreferences.edit().putString(KEY_USER_NAME, value).apply()

    var isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var isAdmin: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_ADMIN, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_ADMIN, value).apply()

    var rememberMe: Boolean
        get() = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_REMEMBER_ME, value).apply()

    var lastLogin: Long
        get() = sharedPreferences.getLong(KEY_LAST_LOGIN, 0L)
        set(value) = sharedPreferences.edit().putLong(KEY_LAST_LOGIN, value).apply()

    fun saveUserSession(usuario: Usuario, rememberMe: Boolean = false) {
        userId = usuario.id
        userEmail = usuario.email
        userName = "${usuario.nombre} ${usuario.apellido}"
        isLoggedIn = true
        isAdmin = usuario.isAdmin
        this.rememberMe = rememberMe
        lastLogin = System.currentTimeMillis()
    }

    fun clearUserSession() {
        sharedPreferences.edit().clear().apply()
    }

    fun isSessionValid(): Boolean {
        if (!isLoggedIn) return false

        // Si no tiene "recordarme" activo, la sesión expira en 24 horas
        if (!rememberMe) {
            val twentyFourHours = 24 * 60 * 60 * 1000L
            val currentTime = System.currentTimeMillis()
            return (currentTime - lastLogin) < twentyFourHours
        }

        // Si tiene "recordarme" activo, la sesión expira en 30 días
        val thirtyDays = 30 * 24 * 60 * 60 * 1000L
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastLogin) < thirtyDays
    }
}