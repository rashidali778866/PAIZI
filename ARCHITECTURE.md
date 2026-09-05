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

## 2. Core Subsystems (Detailed)