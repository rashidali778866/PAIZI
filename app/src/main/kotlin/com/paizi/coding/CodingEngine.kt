package com.paizi.coding

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

/**
 * Coding Engine
 * 
 * Handles:
 * - Code generation
 * - Code editing
 * - File operations
 * - Syntax analysis
 */
class CodingEngine(private val context: Context) {

    /**
     * Create new file
     */
    suspend fun createFile(
        projectPath: String,
        filePath: String,
        content: String = ""
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val file = File(projectPath, filePath)
            file.parentFile?.mkdirs()
            file.writeText(content)
            Timber.d("File created: $filePath")
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to create file: $filePath")
            false
        }
    }

    /**
     * Read file
     */
    suspend fun readFile(projectPath: String, filePath: String): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val file = File(projectPath, filePath)
            if (file.exists()) {
                file.readText()
            } else {
                Timber.w("File not found: $filePath")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to read file: $filePath")
            null
        }
    }

    /**
     * Edit file
     */
    suspend fun editFile(
        projectPath: String,
        filePath: String,
        content: String
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val file = File(projectPath, filePath)
            file.writeText(content)
            Timber.d("File edited: $filePath")
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to edit file: $filePath")
            false
        }
    }

    /**
     * Delete file
     */
    suspend fun deleteFile(
        projectPath: String,
        filePath: String
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val file = File(projectPath, filePath)
            if (file.exists()) {
                file.delete()
                Timber.d("File deleted: $filePath")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete file: $filePath")
            false
        }
    }

    /**
     * List files in directory
     */
    suspend fun listFiles(projectPath: String, dirPath: String = ""): List<FileInfo> = withContext(Dispatchers.IO) {
        return@withContext try {
            val dir = if (dirPath.isEmpty()) {
                File(projectPath)
            } else {
                File(projectPath, dirPath)
            }

            if (!dir.exists()) return@withContext emptyList()

            dir.listFiles()?.map { file ->
                FileInfo(
                    name = file.name,
                    path = file.relativeTo(File(projectPath)).path,
                    isDirectory = file.isDirectory,
                    size = file.length(),
                    lastModified = file.lastModified()
                )
            } ?: emptyList()
        } catch (e: Exception) {
            Timber.e(e, "Failed to list files")
            emptyList()
        }
    }

    /**
     * Search files
     */
    suspend fun searchFiles(
        projectPath: String,
        query: String
    ): List<FileInfo> = withContext(Dispatchers.Default) {
        return@withContext try {
            val projectDir = File(projectPath)
            val results = mutableListOf<FileInfo>()

            projectDir.walkTopDown().forEach { file ->
                if (file.name.contains(query, ignoreCase = true)) {
                    results.add(
                        FileInfo(
                            name = file.name,
                            path = file.relativeTo(projectDir).path,
                            isDirectory = file.isDirectory,
                            size = file.length()
                        )
                    )
                }
            }

            results
        } catch (e: Exception) {
            Timber.e(e, "Failed to search files")
            emptyList()
        }
    }

    /**
     * Analyze code
     */
    suspend fun analyzeCode(code: String, language: String): CodeAnalysis = withContext(Dispatchers.Default) {
        return@withContext try {
            val lines = code.lines()
            val issues = mutableListOf<CodeIssue>()

            // Simple analysis
            lines.forEachIndexed { index, line ->
                if (line.contains("TODO")) {
                    issues.add(
                        CodeIssue(
                            line = index + 1,
                            type = "TODO",
                            message = "Unfinished task",
                            severity = "INFO"
                        )
                    )
                }
                if (line.length > 120) {
                    issues.add(
                        CodeIssue(
                            line = index + 1,
                            type = "STYLE",
                            message = "Line too long",
                            severity = "WARNING"
                        )
                    )
                }
            }

            CodeAnalysis(
                language = language,
                lineCount = lines.size,
                issues = issues,
                complexity = calculateComplexity(code),
                maintainability = 75
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to analyze code")
            CodeAnalysis(
                language = language,
                lineCount = 0,
                error = e.message
            )
        }
    }

    private fun calculateComplexity(code: String): String {
        val funcCount = code.split("fun ").size - 1
        val loopCount = code.split("for ").size + code.split("while ").size - 2
        val ifCount = code.split("if ").size - 1

        val total = funcCount + loopCount + ifCount
        return when {
            total < 5 -> "Low"
            total < 15 -> "Medium"
            else -> "High"
        }
    }
}

data class FileInfo(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long = 0,
    val lastModified: Long = 0
)

data class CodeAnalysis(
    val language: String,
    val lineCount: Int,
    val issues: List<CodeIssue> = emptyList(),
    val complexity: String = "Unknown",
    val maintainability: Int = 0,
    val error: String? = null
)

data class CodeIssue(
    val line: Int,
    val type: String,
    val message: String,
    val severity: String
)
