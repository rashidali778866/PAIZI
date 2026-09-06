package com.paizi.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * PAIZI Database
 * 
 * Stores:
 * - Projects
 * - Conversations
 * - Messages
 * - Tasks
 * - Builds
 * - Tests
 * - Configurations
 */
@Database(
    entities = [
        ProjectEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        TaskEntity::class,
        BuildEntity::class,
        TestResultEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PAIZIDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDAO
    abstract fun conversationDao(): ConversationDAO
    abstract fun messageDao(): MessageDAO
    abstract fun taskDao(): TaskDAO
    abstract fun buildDao(): BuildDAO
    abstract fun testResultDao(): TestResultDAO
}
