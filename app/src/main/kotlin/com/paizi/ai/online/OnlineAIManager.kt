package com.paizi.ai.online

import com.paizi.core.config.AppConfig
import com.paizi.core.config.OnlineAIConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

/**
 * Online AI Manager
 * 
 * Manages:
 * - 10 Online API slots
 * - API configuration
 * - Connection testing
 * - Request handling
 * - Fallback logic
 */
class OnlineAIManager(private val appConfig: AppConfig) {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val retrofitClients = mutableMapOf<Int, Retrofit>()

    /**
     * Configure an API slot
     */
    suspend fun configureSlot(
        slotId: Int,
        config: OnlineAIConfig
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            // Validate configuration
            if (config.apiKey.isEmpty() || config.apiEndpoint.isEmpty()) {
                Timber.w("Invalid configuration for slot $slotId")
                return@withContext false
            }

            // Save configuration
            appConfig.saveOnlineAIConfig(slotId, config)

            // Initialize Retrofit client
            val retrofit = Retrofit.Builder()
                .baseUrl(config.apiEndpoint)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            retrofitClients[slotId] = retrofit
            Timber.i("Slot $slotId configured: ${config.providerName}")
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to configure slot $slotId")
            false
        }
    }

    /**
     * Test API connection
     */
    suspend fun testConnection(slotId: Int): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val config = appConfig.getOnlineAIConfig(slotId) ?: return@withContext false

            if (!config.enabled) {
                Timber.w("Slot $slotId is disabled")
                return@withContext false
            }

            // Simple HTTP GET request to test connectivity
            val client = okHttpClient
            val request = okhttp3.Request.Builder()
                .url(config.apiEndpoint)
                .addHeader("Authorization", "Bearer ${config.apiKey}")
                .build()

            val response = client.newCall(request).execute()
            val success = response.isSuccessful

            Timber.i("Connection test for slot $slotId: ${if (success) "SUCCESS" else "FAILED"}")
            success
        } catch (e: Exception) {
            Timber.e(e, "Connection test failed for slot $slotId")
            false
        }
    }

    /**
     * Call AI with specified slot
     */
    suspend fun callAI(
        slotId: Int,
        prompt: String,
        maxTokens: Int = 1000
    ): AIResponse = withContext(Dispatchers.IO) {
        return@withContext try {
            val config = appConfig.getOnlineAIConfig(slotId)
                ?: return@withContext AIResponse(success = false, error = "Slot not configured")

            if (!config.enabled) {
                return@withContext AIResponse(success = false, error = "Slot is disabled")
            }

            // Create API request based on provider
            val request = createRequest(config, prompt, maxTokens)

            Timber.d("Calling AI at slot $slotId: ${config.providerName}")

            // Make API call (generic implementation)
            val client = okHttpClient
            val httpRequest = okhttp3.Request.Builder()
                .url(config.apiEndpoint)
                .post(
                    okhttp3.RequestBody.create(
                        okhttp3.MediaType.get("application/json"),
                        request
                    )
                )
                .addHeader("Authorization", "Bearer ${config.apiKey}")
                .addHeader("Content-Type", "application/json")
                .build()

            val response = client.newCall(httpRequest).execute()

            if (response.isSuccessful) {
                val body = response.body()?.string() ?: ""
                AIResponse(
                    success = true,
                    content = body,
                    model = config.modelName
                )
            } else {
                AIResponse(
                    success = false,
                    error = "API Error: ${response.code()} - ${response.message()}"
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "AI call failed at slot $slotId")
            AIResponse(success = false, error = e.message ?: "Unknown error")
        }
    }

    /**
     * Get all enabled slots
     */
    fun getEnabledSlots(): List<OnlineAIConfig> {
        return (1..10).mapNotNull { slotId ->
            appConfig.getOnlineAIConfig(slotId)?.takeIf { it.enabled }
        }.sortedBy { it.priority }
    }

    /**
     * Disable a slot
     */
    fun disableSlot(slotId: Int) {
        val config = appConfig.getOnlineAIConfig(slotId)?.copy(enabled = false)
        config?.let { appConfig.saveOnlineAIConfig(slotId, it) }
        retrofitClients.remove(slotId)
    }

    private fun createRequest(config: OnlineAIConfig, prompt: String, maxTokens: Int): String {
        // Generic JSON request format
        return """
            {
                "model": "${config.modelName}",
                "messages": [
                    {
                        "role": "user",
                        "content": "$prompt"
                    }
                ],
                "max_tokens": $maxTokens
            }
        """.trimIndent()
    }
}

data class AIResponse(
    val success: Boolean,
    val content: String = "",
    val model: String = "",
    val error: String? = null,
    val tokensUsed: Int = 0
)
