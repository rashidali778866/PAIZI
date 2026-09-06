package com.paizi.workflow

import com.paizi.agents.base.Task
import com.paizi.agents.executor.AgentExecutor
import com.paizi.agents.executor.ExecutionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Workflow Engine
 * 
 * Orchestrates:
 * - Multi-step workflows
 * - Task dependencies
 * - Error recovery
 * - Parallel execution
 */
class WorkflowEngine(private val agentExecutor: AgentExecutor) {

    /**
     * Execute workflow
     */
    suspend fun executeWorkflow(
        workflow: Workflow
    ): WorkflowResult = withContext(Dispatchers.Default) {
        return@withContext try {
            Timber.i("Executing workflow: ${workflow.name}")

            val results = mutableListOf<ExecutionResult>()
            var currentStep = 0

            for (step in workflow.steps) {
                currentStep++
                Timber.d("Step $currentStep/${workflow.steps.size}: ${step.name}")

                // Check dependencies
                if (!areDependenciesMet(step, results)) {
                    Timber.w("Dependencies not met for step: ${step.name}")
                    continue
                }

                // Execute step
                val stepResults = when (step.type) {
                    StepType.SEQUENTIAL -> {
                        agentExecutor.executeSequential(step.tasks)
                    }
                    StepType.PARALLEL -> {
                        agentExecutor.executeParallel(step.tasks)
                    }
                }

                results.addAll(stepResults)

                // Check for failures
                if (step.stopOnError && stepResults.any { !it.success }) {
                    Timber.e("Step failed and stopOnError is true")
                    return@withContext WorkflowResult(
                        success = false,
                        message = "Workflow stopped at step: ${step.name}",
                        results = results
                    )
                }
            }

            WorkflowResult(
                success = true,
                message = "Workflow completed successfully",
                results = results,
                completedSteps = currentStep
            )
        } catch (e: Exception) {
            Timber.e(e, "Workflow execution failed")
            WorkflowResult(
                success = false,
                error = e.message ?: "Unknown error"
            )
        }
    }

    private fun areDependenciesMet(
        step: WorkflowStep,
        completedResults: List<ExecutionResult>
    ): Boolean {
        if (step.dependsOn.isEmpty()) return true

        return step.dependsOn.all { dependency ->
            completedResults.any { it.agentName == dependency }
        }
    }
}

data class Workflow(
    val id: String,
    val name: String,
    val description: String = "",
    val steps: List<WorkflowStep>
)

data class WorkflowStep(
    val name: String,
    val type: StepType,
    val tasks: List<Task>,
    val dependsOn: List<String> = emptyList(),
    val stopOnError: Boolean = true,
    val retryOnFail: Int = 1
)

enum class StepType {
    SEQUENTIAL,
    PARALLEL
}

data class WorkflowResult(
    val success: Boolean,
    val message: String = "",
    val results: List<ExecutionResult> = emptyList(),
    val completedSteps: Int = 0,
    val error: String? = null,
    val duration: Long = 0
)
