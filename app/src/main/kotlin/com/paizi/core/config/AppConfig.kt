package com.paizi.core.config

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import timber.log.Timber

/**
 * Application Configuration Manager
 * 
 * Manages:
 * - API configurations
 * - Model settings
 * - Security settings
 * - App preferences
 */
class AppConfig(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "paizi_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val gson = Gson()

    // Online AI Configuration
    fun saveOnlineAIConfig(slotId: Int, config: OnlineAIConfig) {
        try {
            val json = gson.toJson(config)
            encryptedPrefs.edit().putString("online_ai_$slotId", json).apply()
            Timber.d("Online AI config saved for slot: $slotId")
        } catch (e: Exception) {
            Timber.e(e, "Failed to save online AI config")
        }
    }

    fun getOnlineAIConfig(slotId: Int): OnlineAIConfig? {
        return try {
            val json = encryptedPrefs.getString("online_ai_$slotId", null)
            json?.let { gson.fromJson(it, OnlineAIConfig::class.java) }
        } catch (e: Exception) {
            Timber.e(e, "Failed to load online AI config")
            null
        }
    }

    fun getAllOnlineAIConfigs(): List<OnlineAIConfig> {
        return (1..10).mapNotNull { getOnlineAIConfig(it) }
    }

    // Offline AI Configuration
    fun saveOfflineAIConfig(modelId: Int, config: OfflineAIConfig) {
        try {
            val json = gson.toJson(config)
            encryptedPrefs.edit().putString("offline_ai_$modelId", json).apply()
            Timber.d("Offline AI config saved for model: $modelId")
        } catch (e: Exception) {
            Timber.e(e, "Failed to save offline AI config")
        }
    }

    fun getOfflineAIConfig(modelId: Int): OfflineAIConfig? {
        return try {
            val json = encryptedPrefs.getString("offline_ai_$modelId", null)
            json?.let { gson.fromJson(it, OfflineAIConfig::class.java) }
        } catch (e: Exception) {
            Timber.e(e, "Failed to load offline AI config")
            null
        }
    }

    fun getAllOfflineAIConfigs(): List<OfflineAIConfig> {
        return (1..5).mapNotNull { getOfflineAIConfig(it) }
    }

    // General Settings
    fun saveSetting(key: String, value: String) {
        encryptedPrefs.edit().putString(key, value).apply()
    }

    fun getSetting(key: String, default: String = ""): String {
        return encryptedPrefs.getString(key, default) ?: default
    }

    fun saveIntSetting(key: String, value: Int) {
        encryptedPrefs.edit().putInt(key, value).apply()
    }

    fun getIntSetting(key: String, default: Int = 0): Int {
        return encryptedPrefs.getInt(key, default)
    }

    fun saveBooleanSetting(key: String, value: Boolean) {
        encryptedPrefs.edit().putBoolean(key, value).apply()
    }

    fun getBooleanSetting(key: String, default: Boolean = false): Boolean {
        return encryptedPrefs.getBoolean(key, default)
    }
}

data class OnlineAIConfig(
    val slotId: Int,
    val providerName: String,
    val apiKey: String, // Will be encrypted
    val apiEndpoint: String,
    val modelName: String,
    val enabled: Boolean = false,
    val priority: Int = 0,
    val customHeaders: Map<String, String> = emptyMap(),
    val timeoutSeconds: Int = 30,
    val rateLimit: Int = 60
)

data class OfflineAIConfig(
    val modelId: Int,
    val modelName: String,
    val modelFile: String,
    val modelPath: String,
    val installed: Boolean = false,
    val enabled: Boolean = false,
    val sizeGB: Double = 0.0,
    val contextLength: Int = 8192,
    val capabilities: List<String> = emptyList(),
    val runtime: String = "llama.cpp"
)
