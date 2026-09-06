package com.paizi.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProjectDAO {
    @Insert
    suspend fun insert(project: ProjectEntity)

    @Update
    suspend fun update(project: ProjectEntity)

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProject(id: String): ProjectEntity?

    @Query("SELECT * FROM projects ORDER BY updated DESC")
    suspend fun getAllProjects(): List<ProjectEntity>

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: String)
}

@Dao
interface ConversationDAO {
    @Insert
    suspend fun insert(conversation: ConversationEntity)

    @Query("SELECT * FROM conversations WHERE projectId = :projectId ORDER BY timestamp DESC")
    suspend fun getConversations(projectId: String): List<ConversationEntity>

    @Query("DELETE FROM conversations WHERE projectId = :projectId")
    suspend fun deleteProjectConversations(projectId: String)
}

@Dao
interface MessageDAO {
    @Insert
    suspend fun insert(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE projectId = :projectId ORDER BY timestamp DESC")
    suspend fun getMessages(projectId: String): List<MessageEntity>
}

@Dao
interface TaskDAO {
    @Insert
    suspend fun insert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE projectId = :projectId")
    suspend fun getProjectTasks(projectId: String): List<TaskEntity>
}

@Dao
interface BuildDAO {
    @Insert
    suspend fun insert(build: BuildEntity)

    @Query("SELECT * FROM builds WHERE projectId = :projectId ORDER BY timestamp DESC")
    suspend fun getBuilds(projectId: String): List<BuildEntity>

    @Query("SELECT * FROM builds WHERE projectId = :projectId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestBuild(projectId: String): BuildEntity?
}

@Dao
interface TestResultDAO {
    @Insert
    suspend fun insert(result: TestResultEntity)

    @Query("SELECT * FROM test_results WHERE projectId = :projectId ORDER BY timestamp DESC")
    suspend fun getTestResults(projectId: String): List<TestResultEntity>
}
