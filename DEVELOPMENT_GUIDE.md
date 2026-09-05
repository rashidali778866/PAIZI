# PAIZI Development Guide

## Getting Started

### Prerequisites
- Android SDK (minimum API 21)
- Kotlin 1.8+
- Gradle 7.0+
- Java 11+
- Git

### Project Setup

```bash
# Clone the repository
git clone https://github.com/rashidali778866/PAIZI.git
cd PAIZI

# Install dependencies
gradle dependencies

# Build the project
gradle build

# Run tests
gradle test
```

## Project Structure

```
PAIZI/
├── app/                          # Main application module
│   ├── src/main/kotlin/com/paizi/
│   │   ├── MainActivity.kt        # Entry point
│   │   ├── ui/                    # UI components
│   │   │   ├── main/             # Main screen
│   │   │   ├── project/          # Project management
│   │   │   ├── editor/           # Code editor
│   │   │   └── settings/         # Settings screen
│   │   ├── agents/               # Agent implementations
│   │   │   ├── base/             # Base agent class
│   │   │   ├── planner/          # Planner agent
│   │   │   ├── coder/            # Coding agent
│   │   │   ├── tester/           # Testing agent
│   │   │   └── builder/          # Build agent
│   │   ├── ai/                   # AI integration
│   │   │   ├── online/           # Online AI APIs
│   │   │   ├── offline/          # Offline models
│   │   │   └── router/           # AI Router
│   │   ├── project/              # Project management
│   │   │   ├── model/            # Project data models
│   │   │   ├── manager/          # Project management
│   │   │   └── memory/           # Project state/memory
│   │   ├── coding/               # Code operations
│   │   │   ├── editor/           # Code editor
│   │   │   └── analyzer/         # Code analysis
│   │   ├── build/                # Build system
│   │   │   ├── gradle/           # Gradle integration
│   │   │   └── apk/              # APK generation
│   │   ├── database/             # Database layer
│   │   │   ├── dao/              # Data access objects
│   │   │   └── entity/           # Entity models
│   │   ├── core/                 # Core utilities
│   │   │   ├── config/           # Configuration
│   │   │   ├── logging/          # Logging system
│   │   │   └── utils/            # Utilities
│   │   └── PAIZIApplication.kt    # Application class
│   ├── src/main/res/             # Resources
│   │   ├── layout/               # Layout XML files
│   │   ├── values/               # String/color resources
│   │   ├── drawable/             # Images and icons
│   │   └── menu/                 # Menu definitions
│   ├── src/test/                 # Unit tests
│   ├── src/androidTest/          # Integration tests
│   ├── build.gradle              # Module build config
│   └── AndroidManifest.xml       # App manifest
├── core/                          # Core library module (optional)
├── gradle/                        # Gradle wrapper
├── build.gradle                  # Root build config
├── settings.gradle               # Gradle settings
├── gradle.properties             # Gradle properties
├── README.md                     # Project readme
├── ARCHITECTURE.md               # Architecture docs
├── SPECIFICATION.md              # Specification
├── IMPLEMENTATION_ROADMAP.md     # Roadmap
└── DEVELOPMENT_GUIDE.md          # This file
```

## Key Modules

### 1. UI Layer (`ui/`)

#### Main Screen
```kotlin
// MainActivity.kt
class MainActivity : AppCompatActivity() {
    // Chat interface
    // Conversation display
    // Input handling
    // Voice support
}
```

#### Project Management UI
```kotlin
// ProjectListScreen.kt
// ProjectDetailScreen.kt
// ProjectWorkspaceScreen.kt
```

#### Settings Screen
```kotlin
// SettingsActivity.kt
// AIConfigurationFragment.kt
// ModelManagementFragment.kt
```

### 2. Agent System (`agents/`)

#### Base Agent
```kotlin
abstract class BaseAgent {
    abstract suspend fun execute(task: Task): Result
    open suspend fun handleError(error: Exception): Result
    open suspend fun retry(task: Task): Result
}
```

#### Specific Agents
```kotlin
class PlannerAgent : BaseAgent()        // Planning
class CoderAgent : BaseAgent()          // Code generation
class TesterAgent : BaseAgent()         // Testing
class DebuggerAgent : BaseAgent()       // Debugging
class BuilderAgent : BaseAgent()        // Building
```

### 3. AI System (`ai/`)

#### Online AI
```kotlin
class OnlineAIManager {
    fun configureSlot(slotId: Int, config: AIConfig)
    suspend fun callAI(slotId: Int, prompt: String): String
    fun testConnection(slotId: Int): Boolean
}
```

#### Offline AI
```kotlin
class OfflineAIManager {
    fun downloadModel(modelId: Int): Flow<DownloadProgress>
    suspend fun inferenceLocal(modelId: Int, prompt: String): String
    fun loadModel(modelId: Int): Boolean
}
```

#### AI Router
```kotlin
class AIRouter {
    suspend fun selectAI(task: Task): AIProvider
    suspend fun executeWithFallback(task: Task): Result
}
```

### 4. Project System (`project/`)

#### Project Manager
```kotlin
class ProjectManager {
    fun createProject(spec: ProjectSpecification): Project
    fun loadProject(projectId: String): Project
    fun saveProject(project: Project)
    fun deleteProject(projectId: String)
}
```

#### Project Memory
```kotlin
class ProjectMemory {
    fun saveState(project: Project)
    fun loadState(projectId: String): ProjectState
    fun updateProjectState(projectId: String, state: Map<String, Any>)
}
```

### 5. Coding Engine (`coding/`)

#### Code Editor
```kotlin
class CodeEditor {
    fun openFile(filePath: String): FileContent
    fun editFile(filePath: String, content: String)
    fun saveFile(filePath: String)
    fun searchCode(query: String): List<SearchResult>
}
```

#### Code Analyzer
```kotlin
class CodeAnalyzer {
    fun analyzeCode(code: String, language: String): AnalysisResult
    fun suggestImprovements(code: String): List<Suggestion>
    fun detectErrors(code: String): List<Error>
}
```

### 6. Build System (`build/`)

#### Gradle Integration
```kotlin
class GradleBuilder {
    fun buildAPK(projectPath: String): BuildResult
    fun runTests(projectPath: String): TestResult
    fun configureGradle(config: BuildConfig)
}
```

### 7. Database (`database/`)

#### DAO Pattern
```kotlin
@Dao
interface ProjectDAO {
    @Insert
    suspend fun insertProject(project: ProjectEntity)
    
    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProject(id: String): ProjectEntity
}
```

## Development Workflow

### 1. Feature Development

```
1. Create feature branch
   git checkout -b feature/agent-system

2. Implement feature
   - Write code
   - Add unit tests
   - Add documentation

3. Test locally
   ./gradlew test
   ./gradlew connectedAndroidTest

4. Commit changes
   git commit -m "feat: Implement agent system"

5. Push to repository
   git push origin feature/agent-system

6. Create pull request
   - Add description
   - Link related issues
   - Request review

7. Code review
   - Address comments
   - Make improvements
   - Ensure quality

8. Merge to main
   - Ensure all tests pass
   - Update documentation
   - Create release notes
```

### 2. Testing Strategy

#### Unit Tests
```kotlin
class AIRouterTest {
    @Test
    fun testOnlineAISelection() {
        // Test AI selection logic
    }
    
    @Test
    fun testFallbackMechanism() {
        // Test fallback
    }
}
```

#### Integration Tests
```kotlin
class ProjectSystemIntegrationTest {
    @Test
    fun testCompleteProjectWorkflow() {
        // Create project
        // Modify files
        // Build project
        // Verify result
    }
}
```

### 3. Debugging

#### Logging
```kotlin
Log.d("PAIZI", "Debug message")
Log.i("PAIZI", "Info message")
Log.w("PAIZI", "Warning message")
Log.e("PAIZI", "Error message", exception)
```

#### Debugging Tools
- Android Studio Debugger
- Logcat
- Profiler
- Network Inspector

## Building & Deployment

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Running on Device
```bash
./gradlew installDebug
adb shell am start -n com.paizi/.MainActivity
```

## Configuration

### gradle.properties
```properties
# Android config
android.useAndroidX=true
android.enableJetifier=true

# Version config
app.versionCode=1
app.versionName=1.0.0

# Build config
org.gradle.jvmargs=-Xmx2048m
```

### local.properties
```properties
sdk.dir=/path/to/android-sdk
ndk.dir=/path/to/android-ndk
```

## Contributing

1. Follow coding standards
2. Write tests for new features
3. Update documentation
4. Create descriptive commit messages
5. Keep commits focused and atomic
6. Ensure all tests pass before submitting PR

## Common Issues

### Build Issues
- Clean build: `./gradlew clean build`
- Update dependencies: `./gradlew dependencies --refresh-dependencies`
- Clear cache: `rm -rf ~/.gradle/caches`

### Runtime Issues
- Check logs: `adb logcat`
- Debug: Use Android Studio debugger
- Profile: Use Android Profiler

## Performance Tips

1. Use coroutines for async operations
2. Implement proper caching
3. Optimize database queries
4. Monitor memory usage
5. Profile regularly
6. Use ProGuard for obfuscation

## Security Best Practices

1. Never hardcode secrets
2. Use secure storage for sensitive data
3. Validate user inputs
4. Implement proper authentication
5. Use HTTPS for API calls
6. Encrypt sensitive data

## Documentation

- Keep documentation updated
- Document complex algorithms
- Add code comments
- Maintain API documentation
- Create user guides
