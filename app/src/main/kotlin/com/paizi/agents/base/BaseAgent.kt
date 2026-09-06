package com.paizi.agents.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Base Agent Abstract Class
 * 
 * Provides:
 * - Task execution framework
 * - State management
 * - Error handling
 * - Retry mechanism
 */
abstract class BaseAgent(
    val agentName: String,
    val agentType: String
) {
    protected val agentScope = CoroutineScope(Dispatchers.Default + Job())
    protected var currentTask: Task? = null
    protected var isExecuting = false

    /**
     * Execute a task
     */
    abstract suspend fun execute(task: Task): Result

    /**
     * Handle errors
     */
    open suspend fun handleError(error: Exception): Result {
        Timber.e(error, "[$agentName] Error handling")
        return Result.Failure(error.message ?: "Unknown error")
    }

    /**
     * Retry logic
     */
    suspend fun retry(
        task: Task,
        maxAttempts: Int = 3,
        delayMs: Long = 1000
    ): Result {
        repeat(maxAttempts) { attempt ->
            try {
                Timber.d("[$agentName] Retry attempt ${attempt + 1}/$maxAttempts")
                val result = execute(task)
                if (result is Result.Success) {
                    return result
                }
                if (attempt < maxAttempts - 1) {
                    kotlinx.coroutines.delay(delayMs)
                }
            } catch (e: Exception) {
                if (attempt == maxAttempts - 1) {
                    return handleError(e)
                }
            }
        }
        return Result.Failure("All retry attempts failed")
    }

    /**
     * Cancel current task
     */
    fun cancel() {
        agentScope.coroutineContext.cancelChildren()
        isExecuting = false
        Timber.d("[$agentName] Task cancelled")
    }

    /**
     * Get agent status
     */
    fun getStatus(): AgentStatus {
        return AgentStatus(
            agentName = agentName,
            agentType = agentType,
            isExecuting = isExecuting,
            currentTask = currentTask?.description
        )
    }
}

sealed class Result {
    data class Success(
        val data: Map<String, Any> = emptyMap(),
        val message: String = ""
    ) : Result()

    data class Failure(
        val error: String,
        val exception: Exception? = null
    ) : Result()
}

data class Task(
    val id: String,
    val type: String,
    val description: String,
    val priority: Int = 0,
    val parameters: Map<String, Any> = emptyMap(),
    val metadata: Map<String, String> = emptyMap()
)

data class AgentStatus(
    val agentName: String,
    val agentType: String,
    val isExecuting: Boolean,
    val currentTask: String?
)
