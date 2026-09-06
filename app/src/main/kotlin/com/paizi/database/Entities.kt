package com.paizi.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val description: String = "",
    val created: Long,
    val updated: Long,
    val status: String,
    val progress: Int,
    val workspace: String
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: String,
    val userMessage: String,
    val aiResponse: String,
    val timestamp: Long,
    val agentType: String
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val content: String,
    val isUser: Boolean,
    val timestamp: Long,
    val projectId: String
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val type: String,
    val description: String,
    val status: String,
    val progress: Int,
    val created: Long,
    val updated: Long
)

@Entity(tableName = "builds")
data class BuildEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val buildNumber: Int,
    val status: String,
    val apkPath: String = "",
    val timestamp: Long,
    val errorLog: String = ""
)

@Entity(tableName = "test_results")
data class TestResultEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val testType: String,
    val passed: Int,
    val failed: Int,
    val timestamp: Long
)
