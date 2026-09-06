package com.paizi.ai.offline

import android.content.Context
import com.paizi.core.config.AppConfig
import com.paizi.core.config.OfflineAIConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

/**
 * Offline AI Manager
 * 
 * Manages:
 * - 5 Offline AI models
 * - Model download
 * - Local inference with llama.cpp
 * - Model lifecycle
 */
class OfflineAIManager(
    private val context: Context,
    private val appConfig: AppConfig
) {

    private val modelsDirectory = File(context.filesDir, "models").also {
        it.mkdirs()
    }

    private val modelConfigs = listOf(
        OfflineAIConfig(
            modelId = 1,
            modelName = "DeepSeek V4 Pro",
            modelFile = "deepseek-v4-pro.gguf",
            sizeGB = 15.0,
            contextLength = 8192,
            capabilities = listOf("code", "reasoning", "math")
        ),
        OfflineAIConfig(
            modelId = 2,
            modelName = "LLaMA 4",
            modelFile = "llama-4.gguf",
            sizeGB = 13.0,
            contextLength = 4096,
            capabilities = listOf("general", "coding")
        ),
        OfflineAIConfig(
            modelId = 3,
            modelName = "Qwen 3.5",
            modelFile = "qwen-3.5.gguf",
            sizeGB = 12.0,
            contextLength = 8192,
            capabilities = listOf("multilingual", "coding")
        ),
        OfflineAIConfig(
            modelId = 4,
            modelName = "Mistral",
            modelFile = "mistral.gguf",
            sizeGB = 7.0,
            contextLength = 8192,
            capabilities = listOf("general", "fast")
        ),
        OfflineAIConfig(
            modelId = 5,
            modelName = "Qwen2.5-Coder 1.5B",
            modelFile = "qwen-coder-1.5b.gguf",
            sizeGB = 1.0,
            contextLength = 4096,
            capabilities = listOf("coding", "lightweight")
        )
    )

    /**
     * Initialize available models
     */
    suspend fun initializeModels() = withContext(Dispatchers.IO) {
        Timber.d("Initializing offline models")
        modelConfigs.forEach { config ->
            val updatedConfig = config.copy(
                modelPath = File(modelsDirectory, config.modelFile).absolutePath,
                installed = File(modelsDirectory, config.modelFile).exists()
            )
            appConfig.saveOfflineAIConfig(config.modelId, updatedConfig)
        }
    }

    /**
     * Download model with progress tracking
     */
    fun downloadModel(modelId: Int): Flow<DownloadProgress> = flow {
        try {
            val config = getModelConfig(modelId) ?: run {
                emit(DownloadProgress(error = "Model not found"))
                return@flow
            }

            Timber.i("Starting download for: ${config.modelName}")
            emit(DownloadProgress(status = "Connecting...", progress = 0))

            // Simulate download progress (in real app, this would be actual HTTP download)
            for (progress in 0..100 step 5) {
                kotlinx.coroutines.delay(100) // Simulate network delay
                emit(DownloadProgress(
                    status = "Downloading ${config.modelName}...",
                    progress = progress,
                    downloadedMB = (config.sizeGB * 1024 * progress / 100).toInt(),
                    totalMB = (config.sizeGB * 1024).toInt()
                ))
            }

            // Mark as installed
            val installedConfig = config.copy(installed = true)
            appConfig.saveOfflineAIConfig(modelId, installedConfig)

            emit(DownloadProgress(
                status = "Download complete",
                progress = 100,
                downloadedMB = (config.sizeGB * 1024).toInt(),
                totalMB = (config.sizeGB * 1024).toInt()
            ))

            Timber.i("Download completed for: ${config.modelName}")
        } catch (e: Exception) {
            Timber.e(e, "Download failed for model $modelId")
            emit(DownloadProgress(error = e.message ?: "Unknown error"))
        }
    }

    /**
     * Local inference with llama.cpp
     */
    suspend fun inferenceLocal(
        modelId: Int,
        prompt: String,
        maxTokens: Int = 512
    ): InferenceResult = withContext(Dispatchers.Default) {
        return@withContext try {
            val config = getModelConfig(modelId) ?: run {
                return@withContext InferenceResult(
                    success = false,
                    error = "Model not found"
                )
            }

            if (!config.installed) {
                return@withContext InferenceResult(
                    success = false,
                    error = "Model not installed"
                )
            }

            Timber.d("Running inference with: ${config.modelName}")

            // Simulate inference (in real app, this would call llama.cpp via JNI)
            val simulatedResponse = generateSimulatedResponse(prompt, config.modelName)

            InferenceResult(
                success = true,
                content = simulatedResponse,
                model = config.modelName,
                tokensGenerated = maxTokens
            )
        } catch (e: Exception) {
            Timber.e(e, "Inference failed for model $modelId")
            InferenceResult(
                success = false,
                error = e.message ?: "Unknown error"
            )
        }
    }

    /**
     * Load model into memory
     */
    suspend fun loadModel(modelId: Int): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val config = getModelConfig(modelId) ?: return@withContext false

            if (!config.installed) {
                Timber.w("Model ${config.modelName} not installed")
                return@withContext false
            }

            Timber.i("Loading model: ${config.modelName}")
            // In real app, this would load the model via llama.cpp JNI
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to load model $modelId")
            false
        }
    }

    /**
     * Unload model
     */
    suspend fun unloadModel(modelId: Int): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val config = getModelConfig(modelId) ?: return@withContext false
            Timber.i("Unloading model: ${config.modelName}")
            // In real app, this would unload the model
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to unload model $modelId")
            false
        }
    }

    /**
     * Delete model
     */
    suspend fun deleteModel(modelId: Int): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val config = getModelConfig(modelId) ?: return@withContext false
            val modelFile = File(config.modelPath)

            if (modelFile.exists()) {
                modelFile.delete()
                val deletedConfig = config.copy(installed = false)
                appConfig.saveOfflineAIConfig(modelId, deletedConfig)
                Timber.i("Model deleted: ${config.modelName}")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete model $modelId")
            false
        }
    }

    /**
     * Get installed models
     */
    fun getInstalledModels(): List<OfflineAIConfig> {
        return (1..5).mapNotNull { modelId ->
            appConfig.getOfflineAIConfig(modelId)?.takeIf { it.installed }
        }
    }

    /**
     * Get enabled models
     */
    fun getEnabledModels(): List<OfflineAIConfig> {
        return (1..5).mapNotNull { modelId ->
            appConfig.getOfflineAIConfig(modelId)?.takeIf { it.enabled && it.installed }
        }
    }

    /**
     * Get available storage
     */
    fun getAvailableStorage(): Long {
        return context.filesDir.parentFile?.freeSpace ?: 0L
    }

    /**
     * Get total models size
     */
    fun getTotalModelsSize(): Long {
        return getInstalledModels().sumOf { (it.sizeGB * 1024 * 1024 * 1024).toLong() }
    }

    private fun getModelConfig(modelId: Int): OfflineAIConfig? {
        return appConfig.getOfflineAIConfig(modelId)
            ?: modelConfigs.firstOrNull { it.modelId == modelId }
    }

    private fun generateSimulatedResponse(prompt: String, modelName: String): String {
        return """Response from $modelName model:
        
Processed prompt: "$prompt"

This is a simulated inference response. In production, this would be generated by llama.cpp running the actual local model.

Key capabilities of this model:
- Understanding and analyzing code
- Generating code snippets
- Explaining concepts
- Multi-language support

Full llama.cpp integration with JNI bindings coming in next phase.
        """.trimIndent()
    }
}

data class DownloadProgress(
    val status: String = "",
    val progress: Int = 0,
    val downloadedMB: Int = 0,
    val totalMB: Int = 0,
    val error: String? = null
)

data class InferenceResult(
    val success: Boolean,
    val content: String = "",
    val model: String = "",
    val tokensGenerated: Int = 0,
    val error: String? = null
)
