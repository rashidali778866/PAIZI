package com.paizi.project.manager

import android.content.Context
import com.google.gson.Gson
import com.paizi.project.memory.ProjectState
import timber.log.Timber
import java.io.File
import java.util.UUID

/**
 * Project Manager
 * 
 * Handles:
 * - Project creation
 * - Project loading
 * - Project saving
 * - File management
 * - State persistence
 */
class ProjectManager(private val context: Context) {

    private val projectsDirectory = File(context.filesDir, "projects").also {
        it.mkdirs()
    }
    private val gson = Gson()

    /**
     * Create new project
     */
    fun createProject(
        name: String,
        type: String,
        requirements: String
    ): Project {
        val projectId = UUID.randomUUID().toString()
        val projectDir = File(projectsDirectory, projectId).also {
            it.mkdirs()
        }

        val project = Project(
            id = projectId,
            name = name,
            type = type,
            created = System.currentTimeMillis(),
            updated = System.currentTimeMillis(),
            requirements = requirements,
            workspace = projectDir.absolutePath
        )

        // Create project state file
        val projectState = ProjectState(
            projectId = projectId,
            projectName = name,
            projectType = type,
            originalRequirements = requirements,
            progress = 0,
            status = "PLANNING"
        )

        saveProject(project)
        saveProjectState(project.id, projectState)

        Timber.i("Project created: $projectId - $name")
        return project
    }

    /**
     * Load project
     */
    fun loadProject(projectId: String): Project? {
        return try {
            val projectFile = File(projectsDirectory, projectId)
            if (!projectFile.exists()) {
                Timber.w("Project not found: $projectId")
                return null
            }

            val metaFile = File(projectFile, "project.json")
            if (metaFile.exists()) {
                val json = metaFile.readText()
                gson.fromJson(json, Project::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to load project: $projectId")
            null
        }
    }

    /**
     * Save project
     */
    fun saveProject(project: Project) {
        try {
            val projectDir = File(projectsDirectory, project.id).also {
                it.mkdirs()
            }
            val metaFile = File(projectDir, "project.json")
            val json = gson.toJson(project)
            metaFile.writeText(json)
            Timber.d("Project saved: ${project.id}")
        } catch (e: Exception) {
            Timber.e(e, "Failed to save project: ${project.id}")
        }
    }

    /**
     * Delete project
     */
    fun deleteProject(projectId: String): Boolean {
        return try {
            val projectDir = File(projectsDirectory, projectId)
            projectDir.deleteRecursively()
            Timber.i("Project deleted: $projectId")
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete project: $projectId")
            false
        }
    }

    /**
     * List all projects
     */
    fun listProjects(): List<Project> {
        return try {
            projectsDirectory.listFiles()?.mapNotNull { dir ->
                if (dir.isDirectory) loadProject(dir.name) else null
            } ?: emptyList()
        } catch (e: Exception) {
            Timber.e(e, "Failed to list projects")
            emptyList()
        }
    }

    /**
     * Get project size
     */
    fun getProjectSize(projectId: String): Long {
        return try {
            val projectDir = File(projectsDirectory, projectId)
            calculateDirSize(projectDir)
        } catch (e: Exception) {
            Timber.e(e, "Failed to calculate project size")
            0L
        }
    }

    /**
     * Save project state (PROJECT_STATE.md)
     */
    fun saveProjectState(projectId: String, state: ProjectState) {
        try {
            val projectDir = File(projectsDirectory, projectId)
            val stateFile = File(projectDir, "PROJECT_STATE.md")
            val markdown = state.toMarkdown()
            stateFile.writeText(markdown)
            Timber.d("Project state saved: $projectId")
        } catch (e: Exception) {
            Timber.e(e, "Failed to save project state: $projectId")
        }
    }

    /**
     * Load project state
     */
    fun loadProjectState(projectId: String): ProjectState? {
        return try {
            val projectDir = File(projectsDirectory, projectId)
            val stateFile = File(projectDir, "PROJECT_STATE.md")
            if (stateFile.exists()) {
                val markdown = stateFile.readText()
                ProjectState.fromMarkdown(markdown)
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to load project state: $projectId")
            null
        }
    }

    private fun calculateDirSize(dir: File): Long {
        var size = 0L
        dir.listFiles()?.forEach { file ->
            size += if (file.isDirectory) calculateDirSize(file) else file.length()
        }
        return size
    }
}

data class Project(
    val id: String,
    val name: String,
    val type: String,
    val created: Long,
    val updated: Long,
    val requirements: String,
    val workspace: String,
    val description: String = "",
    val status: String = "ACTIVE",
    val progress: Int = 0
)
