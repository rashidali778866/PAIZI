package com.paizi.di

import android.content.Context
import androidx.room.Room
import com.paizi.ai.offline.OfflineAIManager
import com.paizi.ai.online.OnlineAIManager
import com.paizi.ai.router.AIRouter
import com.paizi.agents.coder.CoderAgent
import com.paizi.agents.executor.AgentExecutor
import com.paizi.agents.planner.PlannerAgent
import com.paizi.build.BuildSystemManager
import com.paizi.coding.CodingEngine
import com.paizi.core.config.AppConfig
import com.paizi.database.PAIZIDatabase
import com.paizi.project.manager.ProjectManager
import com.paizi.testing.TestingSystemManager
import com.paizi.workflow.WorkflowEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency Injection Module
 * 
 * Provides:
 * - Singleton instances
 * - Database
 * - Services
 * - Managers
 */
@Module
@InstallIn(SingletonComponent::class)
object DIModule {

    @Provides
    @Singleton
    fun provideAppConfig(
        @ApplicationContext context: Context
    ): AppConfig = AppConfig(context)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): PAIZIDatabase = Room.databaseBuilder(
        context,
        PAIZIDatabase::class.java,
        "paizi.db"
    ).build()

    @Provides
    @Singleton
    fun provideOnlineAIManager(
        appConfig: AppConfig
    ): OnlineAIManager = OnlineAIManager(appConfig)

    @Provides
    @Singleton
    fun provideOfflineAIManager(
        @ApplicationContext context: Context,
        appConfig: AppConfig
    ): OfflineAIManager = OfflineAIManager(context, appConfig)

    @Provides
    @Singleton
    fun provideAIRouter(
        onlineAIManager: OnlineAIManager,
        offlineAIManager: OfflineAIManager,
        appConfig: AppConfig
    ): AIRouter = AIRouter(onlineAIManager, offlineAIManager, appConfig)

    @Provides
    @Singleton
    fun providePlannerAgent(): PlannerAgent = PlannerAgent()

    @Provides
    @Singleton
    fun provideCoderAgent(): CoderAgent = CoderAgent()

    @Provides
    @Singleton
    fun provideAgentExecutor(
        plannerAgent: PlannerAgent,
        coderAgent: CoderAgent
    ): AgentExecutor {
        val executor = AgentExecutor()
        executor.registerAgent(plannerAgent)
        executor.registerAgent(coderAgent)
        return executor
    }

    @Provides
    @Singleton
    fun provideWorkflowEngine(
        agentExecutor: AgentExecutor
    ): WorkflowEngine = WorkflowEngine(agentExecutor)

    @Provides
    @Singleton
    fun provideProjectManager(
        @ApplicationContext context: Context
    ): ProjectManager = ProjectManager(context)

    @Provides
    @Singleton
    fun provideCodingEngine(
        @ApplicationContext context: Context
    ): CodingEngine = CodingEngine(context)

    @Provides
    @Singleton
    fun provideBuildSystemManager(
        @ApplicationContext context: Context
    ): BuildSystemManager = BuildSystemManager(context)

    @Provides
    @Singleton
    fun provideTestingSystemManager(): TestingSystemManager = TestingSystemManager()
}
