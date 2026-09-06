package com.paizi.ai.router

import com.paizi.ai.offline.OfflineAIManager
import com.paizi.ai.online.OnlineAIManager
import com.paizi.ai.online.AIResponse
import com.paizi.core.config.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * AI Router - Intelligent AI Decision Engine
 * 
 * Selects the best AI provider based on:
 * - Task type and requirements
 * - Model capabilities
 * - Availability and performance
 * - User preferences
 * - Device resources
 */
class AIRouter(
    private val onlineAIManager: OnlineAIManager,
    private val offlineAIManager: OfflineAIManager,
    private val appConfig: AppConfig
) {

    /**
     * Execute AI task with intelligent routing
     */
    suspend fun executeTask(
        prompt: String,
        taskType: AITaskType = AITaskType.GENERAL,
        preferOffline: Boolean = false,
        maxTokens: Int = 1000
    ): AITaskResult = withContext(Dispatchers.Default) {
        return@withContext try {
            Timber.d("Routing task: $taskType")

            // Step 1: Determine AI mode
            val aiMode = determineAIMode(taskType, preferOffline)
            Timber.d("Selected AI mode: $aiMode")

            // Step 2: Execute based on mode
            val result = when (aiMode) {
                AIMode.ONLINE_ONLY -> executeOnline(prompt, maxTokens)
                AIMode.OFFLINE_ONLY -> executeOffline(prompt, maxTokens)
                AIMode.HYBRID -> executeHybrid(prompt, maxTokens)
                AIMode.UNAVAILABLE -> AITaskResult(
                    success = false,
                    error = "No AI providers available"
                )
            }

            result
        } catch (e: Exception) {
            Timber.e(e, "Task execution failed")
            AITaskResult(success = false, error = e.message ?: "Unknown error")
        }
    }

    /**
     * Execute with automatic fallback
     */
    suspend fun executeWithFallback(
        prompt: String,
        taskType: AITaskType = AITaskType.GENERAL
    ): AITaskResult = withContext(Dispatchers.Default) {
        return@withContext try {
            Timber.i("Executing with fallback: $taskType")

            // Try online AI first
            val onlineResult = executeOnline(prompt)
            if (onlineResult.success) {
                return@withContext onlineResult
            }

            Timber.w("Online AI failed, trying offline")

            // Fallback to offline AI
            val offlineResult = executeOffline(prompt)
            if (offlineResult.success) {
                return@withContext offlineResult
            }

            Timber.e("Both online and offline AI failed")
            AITaskResult(success = false, error = "All AI providers failed")
        } catch (e: Exception) {
            Timber.e(e, "Fallback execution failed")
            AITaskResult(success = false, error = e.message ?: "Unknown error")
        }
    }

    /**
     * Execute using online AI
     */
    private suspend fun executeOnline(
        prompt: String,
        maxTokens: Int = 1000
    ): AITaskResult {
        val enabledSlots = onlineAIManager.getEnabledSlots()

        for (slot in enabledSlots) {
            try {
                Timber.d("Trying online AI slot: ${slot.slotId} (${slot.providerName})")
                val response = onlineAIManager.callAI(slot.slotId, prompt, maxTokens)

                if (response.success) {
                    return AITaskResult(
                        success = true,
                        content = response.content,
                        model = response.model,
                        aiMode = "Online",
                        provider = slot.providerName
                    )
                }
            } catch (e: Exception) {
                Timber.w(e, "Slot ${slot.slotId} failed, trying next")
                continue
            }
        }

        return AITaskResult(
            success = false,
            error = "All online AI slots failed"
        )
    }

    /**
     * Execute using offline AI
     */
    private suspend fun executeOffline(
        prompt: String,
        maxTokens: Int = 512
    ): AITaskResult {
        val enabledModels = offlineAIManager.getEnabledModels()

        for (model in enabledModels) {
            try {
                Timber.d("Trying offline AI model: ${model.modelId} (${model.modelName})")
                val result = offlineAIManager.inferenceLocal(
                    model.modelId,
                    prompt,
                    maxTokens
                )

                if (result.success) {
                    return AITaskResult(
                        success = true,
                        content = result.content,
                        model = result.model,
                        aiMode = "Offline",
                        tokensGenerated = result.tokensGenerated
                    )
                }
            } catch (e: Exception) {
                Timber.w(e, "Model ${model.modelId} failed, trying next")
                continue
            }
        }

        return AITaskResult(
            success = false,
            error = "All offline AI models failed"
        )
    }

    /**
     * Execute using both online and offline
     */
    private suspend fun executeHybrid(
        prompt: String,
        maxTokens: Int = 1000
    ): AITaskResult {
        // Prefer online for better quality, fallback to offline
        return executeWithFallback(prompt)
    }

    /**
     * Determine which AI mode to use
     */
    private fun determineAIMode(
        taskType: AITaskType,
        preferOffline: Boolean
    ): AIMode {
        val onlineAvailable = onlineAIManager.getEnabledSlots().isNotEmpty()
        val offlineAvailable = offlineAIManager.getEnabledModels().isNotEmpty()

        return when {
            preferOffline && offlineAvailable -> {
                if (onlineAvailable) AIMode.HYBRID else AIMode.OFFLINE_ONLY
            }
            onlineAvailable && offlineAvailable -> AIMode.HYBRID
            onlineAvailable -> AIMode.ONLINE_ONLY
            offlineAvailable -> AIMode.OFFLINE_ONLY
            else -> AIMode.UNAVAILABLE
        }
    }

    /**
     * Get router status
     */
    suspend fun getStatus(): RouterStatus = withContext(Dispatchers.Default) {
        return@withContext RouterStatus(
            onlineProvidersCount = onlineAIManager.getEnabledSlots().size,
            offlineModelsCount = offlineAIManager.getEnabledModels().size,
            onlineAvailable = onlineAIManager.getEnabledSlots().isNotEmpty(),
            offlineAvailable = offlineAIManager.getEnabledModels().isNotEmpty(),
            preferredMode = when {
                offlineAIManager.getEnabledModels().isNotEmpty() -> "Offline"
                onlineAIManager.getEnabledSlots().isNotEmpty() -> "Online"
                else -> "None"
            }
        )
    }
}

enum class AITaskType {
    GENERAL,
    CODE_GENERATION,
    CODE_ANALYSIS,
    TESTING,
    DEBUGGING,
    DOCUMENTATION,
    PLANNING,
    REASONING
}

enum class AIMode {
    ONLINE_ONLY,
    OFFLINE_ONLY,
    HYBRID,
    UNAVAILABLE
}

data class AITaskResult(
    val success: Boolean,
    val content: String = "",
    val model: String = "",
    val aiMode: String = "",
    val provider: String = "",
    val tokensGenerated: Int = 0,
    val error: String? = null,
    val executionTimeMs: Long = 0
)

data class RouterStatus(
    val onlineProvidersCount: Int,
    val offlineModelsCount: Int,
    val onlineAvailable: Boolean,
    val offlineAvailable: Boolean,
    val preferredMode: String
)
