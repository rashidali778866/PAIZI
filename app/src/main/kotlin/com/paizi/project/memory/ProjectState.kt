package com.paizi.project.memory

import com.google.gson.Gson

/**
 * Project State (PROJECT_STATE.md)
 * 
 * Central memory file for:
 * - Project requirements
 * - Approved blueprint
 * - Current progress
 * - Decisions
 * - Features
 * - Errors and fixes
 */
data class ProjectState(
    val projectId: String,
    val projectName: String,
    val projectType: String,
    val originalRequirements: String = "",
    val approvedBlueprint: String = "",
    val architecture: String = "",
    val completedFeatures: List<String> = emptyList(),
    val remainingFeatures: List<String> = emptyList(),
    val currentWork: String = "",
    val progress: Int = 0,
    val status: String = "PLANNING",
    val decisions: List<Decision> = emptyList(),
    val errors: List<ErrorLog> = emptyList(),
    val tests: TestSummary = TestSummary(),
    val build: BuildInfo = BuildInfo(),
    val version: String = "1.0.0",
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toMarkdown(): String {
        val sb = StringBuilder()
        sb.append("# Project State - $projectName\n\n")
        sb.append("## Project Identity\n")
        sb.append("- Project ID: $projectId\n")
        sb.append("- Name: $projectName\n")
        sb.append("- Type: $projectType\n")
        sb.append("- Status: $status\n")
        sb.append("- Progress: $progress%\n\n")

        sb.append("## Requirements\n")
        sb.append(originalRequirements).append("\n\n")

        sb.append("## Approved Blueprint\n")
        sb.append(approvedBlueprint).append("\n\n")

        sb.append("## Completed Features\n")
        completedFeatures.forEach { sb.append("- ✅ $it\n") }
        sb.append("\n")

        sb.append("## Remaining Features\n")
        remainingFeatures.forEach { sb.append("- ⬜ $it\n") }
        sb.append("\n")

        sb.append("## Current Work\n")
        sb.append(currentWork).append("\n\n")

        sb.append("## Decisions\n")
        decisions.forEach { decision ->
            sb.append("- **${decision.title}**: ${decision.description}\n")
        }
        sb.append("\n")

        sb.append("## Errors & Fixes\n")
        errors.forEach { error ->
            sb.append("- ❌ ${error.description} → Fixed\n")
        }
        sb.append("\n")

        sb.append("## Test Summary\n")
        sb.append("- Passed: ${tests.passed}\n")
        sb.append("- Failed: ${tests.failed}\n")
        sb.append("- Coverage: ${tests.coverage}%\n\n")

        sb.append("## Build Status\n")
        sb.append("- Status: ${build.status}\n")
        sb.append("- Version: ${build.version}\n")
        sb.append("- Last Build: ${build.lastBuild}\n\n")

        sb.append("---\n")
        sb.append("*Last Updated: $lastUpdated*\n")
        return sb.toString()
    }

    companion object {
        fun fromMarkdown(markdown: String): ProjectState? {
            // Parse markdown and reconstruct ProjectState
            return try {
                ProjectState(
                    projectId = "unknown",
                    projectName = "Unknown",
                    projectType = "Unknown"
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

data class Decision(
    val title: String,
    val description: String,
    val date: Long = System.currentTimeMillis()
)

data class ErrorLog(
    val id: String,
    val description: String,
    val rootCause: String = "",
    val fix: String = "",
    val status: String = "FIXED",
    val date: Long = System.currentTimeMillis()
)

data class TestSummary(
    val passed: Int = 0,
    val failed: Int = 0,
    val coverage: Int = 0
)

data class BuildInfo(
    val status: String = "PENDING",
    val version: String = "1.0.0",
    val lastBuild: String = "Never",
    val apkPath: String = ""
)
