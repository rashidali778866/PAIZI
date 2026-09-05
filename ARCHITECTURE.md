# PAIZI - مکمل Architecture

## 1. System Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    PAIZI MAIN APPLICATION                   │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         PRESENTATION LAYER (Mobile UI)               │   │
│  │  • Main Screen (Chat Interface)                      │   │
│  │  • Hamburger Menu                                    │   │
│  │  • Project Screens                                   │   │
│  │  • Settings Screen                                   │   │
│  │  • Code Editor Screen                               │   │
│  │  • Build/Test Status Screen                         │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         APPLICATION LOGIC LAYER                      │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌────────────┐ │   │
│  │  │ Agent System │  │  AI Systems  │  │  Workflow  │ │   │
│  │  │              │  │              │  │  Engine    │ │   │
│  │  │ • Planner    │  │ • Router     │  │            │ │   │
│  │  │ • Engineer   │  │ • Online AI  │  │ Multi-step │ │   │
│  │  │ • Coder      │  │ • Offline AI │  │ tasks      │ │   │
│  │  │ • Tester     │  │ • Fallback   │  │            │ │   │
│  │  └──────────────┘  └──────────────┘  └────────────┘ │   │
│  │                                                        │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌────────────┐ │   │
│  │  │ Command      │  │  Coding      │  │ Build &    │ │   │
│  │  │ Router       │  │  Engine      │  │ Test       │ │   │
│  │  │              │  │              │  │ System     │ │   │
│  │  │ Routes tasks │  │ Code edit,   │  │            │ │   │
│  │  │ to agents    │  │ analysis,    │  │ Gradle,    │ │   │
│  │  └──────────────┘  │ refactoring  │  │ APK Gen    │ │   │
│  │                    └──────────────┘  └────────────┘ │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         DATA & STATE LAYER                           │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌────────────┐ │   │
│  │  │ Project      │  │  Project     │  │  Persistent│ │   │
│  │  │ Management   │  │  Memory      │  │  Storage   │ │   │
│  │  │              │  │              │  │            │ │   │
│  │  │ • Projects   │  │ PROJECT_     │  │ SQLite DB  │ │   │
│  │  │ • Files      │  │ STATE.md     │  │            │ │   │
│  │  │ • Tasks      │  │ • History    │  │ • Projects │ │   │
│  │  │ • Features   │  │ • Decisions  │  │ • Messages │ │   │
│  │  └──────────────┘  │ • Progress   │  │ • Memory   │ │   │
│  │                    └──────────────┘  └────────────┘ │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         EXTERNAL INTEGRATIONS                        │   │
│  │  • Online AI APIs (10 Slots)                         │   │
│  │  • Offline Models (llama.cpp)                        │   │
│  │  • Android SDK / Gradle                             │   │
│  │  • File System                                       │   │
│  │  • Device Resources                                 │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

## 2. Core Subsystems

### 2.1 AI System Architecture

```
                    USER REQUEST
                         ↓
                  ┌─────────────────┐
                  │   AI ROUTER     │
                  │  (Decision      │
                  │   Engine)       │
                  └────────┬────────┘
                           │
        ┌──────────────────┼──────────────────┐
        ↓                  ↓                  ↓
   ┌─────────┐      ┌──────────┐      ┌────────────┐
   │ Online  │      │ Online   │      │  Offline   │
   │   AI 1  │      │   AI 2   │      │  Model 1   │
   │(Fallback)      │(Fallback)       │(Fallback)  │
   │ API Slot│      │ API Slot │      │ llama.cpp  │
   └─────────┘      └──────────┘      └────────────┘
        │                 │                  │
        └─────────────────┼──────────────────┘
                          ↓
                   ┌─────────────┐
                   │   RESPONSE  │
                   │   HANDLING  │
                   └─────────────┘
                         ↓
                    USER DISPLAY
```

### 2.2 Agent System Architecture

```
                   COMMAND ROUTER
                        ↓
        ┌───────────────────────────────┐
        ↓       ↓      ↓       ↓        ↓
   ┌─────────┐ ┌────┐ ┌────┐ ┌────┐ ┌─────┐
   │ Planner │ │Code│ │Test│ │Dbug│ │Build│
   │ Agent   │ │Agnt│ │Agnt│ │Agnt│ │Agnt │
   └─────────┘ └────┘ └────┘ └────┘ └─────┘
        ↓        ↓     ↓      ↓      ↓
   ┌─────────────────────────────────────┐
   │    AGENT EXECUTOR                   │
   │  (Task Execution, State, Retry)     │
   └─────────────────────────────────────┘
        ↓
   ┌─────────────────────────────────────┐
   │    WORKFLOW ENGINE                  │
   │  (Multi-step task orchestration)    │
   └─────────────────────────────────────┘
```

### 2.3 Project Management System

```
PROJECT WORKSPACE
    ↓
┌───────────────────────────────────┐
│  Project Metadata                 │
│  • ID, Name, Type                 │
│  • Platform, Technology Stack     │
└───────────────────────────────────┘
    ↓
┌───────────────────────────────────┐
│  PROJECT_STATE.md (Central Memory)│
│  • Requirements                   │
│  • Approved Blueprint             │
│  • Architecture                   │
│  • Completed Features             │
│  • Current Work                   │
│  • Decisions                      │
│  • Progress (%)                   │
│  • Errors & Fixes                 │
│  • Build/Test Status              │
└───────────────────────────────────┘
    ↓
┌───────────────────────────────────┐
│  Project Files & Workspace        │
│  • Source Code                    │
│  • Resources                      │
│  • Build Files                    │
│  • Tests                          │
│  • Builds (APKs, ZIPs)            │
└───────────────────────────────────┘
    ↓
┌───────────────────────────────────┐
│  Project History & Analytics      │
│  • Conversation Log               │
│  • Change Timeline                │
│  • Version History                │
│  • Build History                  │
│  • Test Results                   │
└───────────────────────────────────┘
```

### 2.4 Development Workflow

```
STEP 1: REQUIREMENT ANALYSIS
  Input: User description
  Process: 
    • Parse requirement
    • Identify missing info
    • Generate clarifying questions
    • Analyze dependencies
    • Identify technical needs
  Output: Complete requirement set

STEP 2: BLUEPRINT GENERATION
  Input: Complete requirements
  Process:
    • Generate technical architecture
    • Design UI/UX
    • Plan modules & features
    • Define data flow
    • Create visual mockups
  Output: Complete approved blueprint

STEP 3: IMPLEMENTATION
  Input: Approved blueprint
  Process:
    • Setup project structure
    • Generate code skeleton
    • Implement features (one by one)
    • Write tests
    • Update PROJECT_STATE.md
  Output: Working code

STEP 4: TESTING & DEBUGGING
  Input: Implementation
  Process:
    • Run tests
    • Detect errors
    • Analyze root cause
    • Apply fixes
    • Retest
  Output: Error-free code

STEP 5: BUILD & VERIFICATION
  Input: Tested code
  Process:
    • Configure build system
    • Compile
    • Generate APK/Package
    • Verify runtime
  Output: Final deliverable

STEP 6: DELIVERY
  Input: Verified build
  Process:
    • Generate documentation
    • Package source code
    • Create project ZIP
    • Save project state
  Output: Complete project package
```

## 3. Data Model

### 3.1 Project Structure

```
PAIZI/
├── projects/
│   ├── {PROJECT_ID}/
│   │   ├── PROJECT_STATE.md (Central Memory File)
│   │   ├── requirements.md
│   │   ├── blueprint.md
│   │   ├── architecture.md
│   │   ├── src/
│   │   │   ├── java/
│   │   │   ├── kotlin/
│   │   │   ├── resources/
│   │   │   └── ... (as per project type)
│   │   ├── build/
│   │   ├── tests/
│   │   ├── build.gradle
│   │   ├── AndroidManifest.xml (if Android)
│   │   ├── build_artifacts/
│   │   │   ├── app.apk
│   │   │   └── project_backup.zip
│   │   ├── conversation_history.json
│   │   ├── decisions.json
│   │   ├── errors_log.json
│   │   └── version_history.json
│   └── ...more projects
├── models/
│   ├── offline_models/
│   │   ├── deepseek-v4-pro.gguf
│   │   ├── llama-4.gguf
│   │   └── ...
│   └── model_registry.json
├── config/
│   ├── ai_providers.json (10 Online slots)
│   ├── offline_models.json
│   ├── app_settings.json
│   └── security.json
└── database/
    └── paizi.db (SQLite)
```

### 3.2 PROJECT_STATE.md Structure

```markdown
# Project State - {PROJECT_NAME}

## Project Identity
- Project ID: {UUID}
- Name: {Name}
- Type: {Android/Web/Desktop/etc}
- Created: {Date}
- Last Updated: {Date}

## Original Requirements
- {Requirement 1}
- {Requirement 2}
- ...

## Approved Blueprint
[Approved plan with all details]

## Architecture
[Architecture diagram and description]

## Modules
- Module 1: [Status]
- Module 2: [Status]
- ...

## Completed Features
- [ ] Feature 1 - Implementation Status
- [x] Feature 2 - ✅ Complete
- ...

## Current Work
- Currently working on: [Feature Name]
- Progress: XX%
- Next step: [Next Task]

## Remaining Features
- [ ] Feature X
- [ ] Feature Y
- ...

## Important Decisions
- Decision 1: [Rationale]
- Decision 2: [Rationale]
- ...

## Errors & Fixes
### Error 1
- Description: [What went wrong]
- Root Cause: [Why]
- Fix Applied: [Solution]
- Status: ✅ Fixed / 🔄 In Progress / ❌ Pending

## Tests
- Unit Tests: [Count] Passing
- Integration Tests: [Count] Passing
- Total Test Coverage: XX%

## Build Status
- Last Build: [Status] - {Date}
- APK Location: {Path}
- Version: {Version}
- Build Artifacts: [Listed]

## Progress Tracking
- Overall Progress: XX%
- Module Completion:
  - Module 1: XX%
  - Module 2: XX%
  - ...

## Version History
- v1.0 - [Major changes]
- v0.9 - [Features added]
- ...

## Critical Notes
[Any critical information]
```

## 4. AI System Configuration

### 4.1 Online AI Slots (10 Generic)

```json
{
  "online_ai_slots": [
    {
      "slot_id": 1,
      "provider_name": "API Provider Name",
      "api_key": "***SECURE***",
      "api_endpoint": "https://api.provider.com/v1/chat",
      "model_name": "model-name",
      "headers": { "custom": "header" },
      "enabled": true,
      "priority": 1,
      "capabilities": ["chat", "code", "analysis"],
      "rate_limit": 60,
      "timeout": 30
    },
    ... (Slots 2-10 similar)
  ]
}
```

### 4.2 Offline AI Models (5 Slots)

```json
{
  "offline_models": [
    {
      "model_id": 1,
      "model_name": "DeepSeek V4 Pro",
      "model_file": "deepseek-v4-pro.gguf",
      "location": "/models/offline_models/",
      "size_gb": 15.0,
      "installed": true,
      "enabled": true,
      "context_length": 8192,
      "capabilities": ["code", "reasoning"],
      "runtime": "llama.cpp"
    },
    ... (Models 2-5 similar)
  ]
}
```

## 5. Security Architecture

```
SECURITY LAYER
├── API Key Management
│   ├── Encrypted storage
│   ├── No hardcoding
│   └── No logging
├── Project Isolation
│   ├── Separate workspaces
│   ├── File permissions
│   └── Access control
├── Safe Operations
│   ├── Sandboxed execution
│   ├── Command validation
│   └── Resource limits
└── Audit Trail
    ├── Activity logging
    ├── No sensitive data
    └── Security events
```

## 6. Database Schema (SQLite)

```sql
-- Core Tables

TABLE projects {
  id UUID PRIMARY KEY,
  name TEXT,
  type TEXT,
  created_at DATETIME,
  updated_at DATETIME,
  status TEXT,
  progress INTEGER,
  workspace_path TEXT
}

TABLE conversations {
  id INTEGER PRIMARY KEY,
  project_id UUID,
  user_message TEXT,
  ai_response TEXT,
  timestamp DATETIME,
  agent_type TEXT
}

TABLE project_state {
  id INTEGER PRIMARY KEY,
  project_id UUID,
  state_data TEXT (JSON),
  version INTEGER,
  updated_at DATETIME
}

TABLE tasks {
  id INTEGER PRIMARY KEY,
  project_id UUID,
  task_type TEXT,
  status TEXT,
  progress INTEGER,
  created_at DATETIME
}

TABLE ai_providers {
  id INTEGER PRIMARY KEY,
  slot_number INTEGER,
  provider_name TEXT,
  api_endpoint TEXT,
  enabled BOOLEAN,
  priority INTEGER
}

TABLE offline_models {
  id INTEGER PRIMARY KEY,
  model_name TEXT,
  model_path TEXT,
  installed BOOLEAN,
  enabled BOOLEAN,
  size_gb REAL
}

TABLE builds {
  id INTEGER PRIMARY KEY,
  project_id UUID,
  build_number INTEGER,
  status TEXT,
  apk_path TEXT,
  timestamp DATETIME
}

TABLE test_results {
  id INTEGER PRIMARY KEY,
  project_id UUID,
  test_type TEXT,
  passed INTEGER,
  failed INTEGER,
  timestamp DATETIME
}

TABLE errors {
  id INTEGER PRIMARY KEY,
  project_id UUID,
  error_message TEXT,
  root_cause TEXT,
  status TEXT,
  created_at DATETIME
}

TABLE decisions {
  id INTEGER PRIMARY KEY,
  project_id UUID,
  decision TEXT,
  rationale TEXT,
  timestamp DATETIME
}
```

This comprehensive architecture provides the foundation for PAIZI's complete implementation.
