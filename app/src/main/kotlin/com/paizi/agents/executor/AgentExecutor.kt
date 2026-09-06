package com.paizi.agents.executor

import com.paizi.agents.base.BaseAgent
import com.paizi.agents.base.Result
import com.paizi.agents.base.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Agent Executor
 * 
 * Orchestrates:
 * - Multiple agent execution
 * - Task queuing
 * - Result aggregation
 * - Error handling
 */
class AgentExecutor {

    private val agents = mutableMapOf<String, BaseAgent>()
    private val taskQueue = mutableListOf<Task>()
    private var isRunning = false

    fun registerAgent(agent: BaseAgent) {
        agents[agent.agentType] = agent
        Timber.d("Agent registered: ${agent.agentName}")
    }

    suspend fun executeTask(task: Task): ExecutionResult = withContext(Dispatchers.Default) {
        return@withContext try {
            Timber.d("Executing task: ${task.id}")

            val agent = agents[task.type] ?: run {
                Timber.e("No agent found for task type: ${task.type}")
                return@withContext ExecutionResult(
                    success = false,
                    error = "No agent available for task type: ${task.type}"
                )
            }

            val result = agent.execute(task)

            when (result) {
                is Result.Success -> {
                    ExecutionResult(
                        success = true,
                        data = result.data,
                        message = result.message,
                        agentName = agent.agentName
                    )
                }
                is Result.Failure -> {
                    ExecutionResult(
                        success = false,
                        error = result.error,
                        agentName = agent.agentName
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Task execution failed")
            ExecutionResult(
                success = false,
                error = e.message ?: "Unknown error"
            )
        }
    }

    suspend fun executeSequential(tasks: List<Task>): List<ExecutionResult> {
        val results = mutableListOf<ExecutionResult>()
        for (task in tasks) {
            val result = executeTask(task)
            results.add(result)
            if (!result.success) {
                Timber.w("Task ${task.id} failed, stopping sequence")
                break
            }
        }
        return results
    }

    suspend fun executeParallel(tasks: List<Task>): List<ExecutionResult> {
        return withContext(Dispatchers.Default) {
            tasks.map { task ->
                executeTask(task)
            }
        }
    }

    fun queueTask(task: Task) {
        taskQueue.add(task)
        Timber.d("Task queued: ${task.id} (Queue size: ${taskQueue.size})")
    }

    suspend fun processQueue() {
        isRunning = true
        while (isRunning && taskQueue.isNotEmpty()) {
            val task = taskQueue.removeAt(0)
            executeTask(task)
        }
        isRunning = false
    }

    fun stopProcessing() {
        isRunning = false
        Timber.d("Queue processing stopped")
    }

    fun getAgentStatus(): List<String> {
        return agents.values.map { agent ->
            "${agent.agentName}: ${if (agent.getStatus().isExecuting) "EXECUTING" else "IDLE"}"
        }
    }
}

data class ExecutionResult(
    val success: Boolean,
    val data: Map<String, Any> = emptyMap(),
    val message: String = "",
    val error: String? = null,
    val agentName: String = "",
    val executionTimeMs: Long = 0
)
