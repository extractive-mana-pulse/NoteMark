<img style="width: 100%; height: auto;" alt="image" src="https://github.com/user-attachments/assets/047dafde-2f3d-4b6d-b791-341f68ab7389" />

# 📝 NoteMark

A modern Android note-taking application built with **Kotlin** and **Jetpack Compose**, following a clean, multi-module architecture organized by feature.

---

## 🏗️ Architecture

NoteMark follows a **multi-module, feature-based architecture** with a clear separation of concerns across three layers in each feature module:

```
NoteMark/
├── app/                    # Application entry point
├── auth/
│   ├── data/               # Auth data sources & repositories
│   ├── domain/             # Auth business logic & use cases
│   └── presentation/       # Auth UI (login, register screens)
├── note/
│   ├── data/               # Note data sources & repositories
│   ├── domain/             # Note business logic & use cases
│   └── presentation/       # Note UI (list, detail, editor)
├── core/
│   ├── data/               # Shared data utilities
│   ├── domain/             # Shared models & interfaces
│   └── presentation/       # Shared UI components
├── releases/
│   ├── data/
│   ├── domain/
│   └── presentation/
└── build-logic/            # Convention plugins & shared build config
```

Each feature module is split into `data`, `domain`, and `presentation` layers, ensuring testability and scalability.

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| DI | Dagger Hilt 2.56.2 |
| Build System | Gradle with Kotlin DSL & Convention Plugins |
| Code Generation | KSP (Kotlin Symbol Processing) |
| Serialization | Kotlin Serialization |
| Architecture | Multi-module, Clean Architecture (MVI/MVVM) |

---

## 📦 Modules

| Module | Description |
|---|---|
| `:app` | Main application module, ties all features together |
| `:auth` | User authentication — login & registration flows |
| `:note` | Core note-taking feature — create, read, update, delete notes |
| `:core` | Shared utilities, base classes, and common UI components |
| `:releases` | GitHub releases browsing & changelog display |
| `:build-logic` | Shared Gradle convention plugins for consistent build config |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17+
- Android SDK with minimum API level configured in the project

### Clone the repository

```bash
git clone https://github.com/extractive-mana-pulse/NoteMark.git
cd NoteMark
```

### Build & Run

Open the project in Android Studio and run the `:app` module on an emulator or physical device, or build via the command line:

```bash
./gradlew :app:assembleDebug
```

---

## 🔧 Build Configuration

This project uses **Gradle Convention Plugins** (`build-logic` module) to share and centralize build configuration across all modules — keeping individual `build.gradle.kts` files clean and minimal.

Type-safe project accessors are enabled via:

```kotlin
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
```

---

## 🗺️ Roadmap

Planned features and improvements tracked in the issue tracker:

- [ ] **Theme support** — Light / Dark / System theme switching
- [ ] **GitHub Releases integration** — In-app changelog and release notes
- [ ] **Feature-based modularization** — Full multi-module migration (in progress)
- [ ] **Status bar fix** — Proper edge-to-edge status bar handling
- [ ] **Code rework** — General refactoring and quality improvements

---

## 🤝 Contributing

Contributions are welcome! Feel free to open an issue or submit a pull request.

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m 'Add your feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---

## 📄 License

This project is open source. See the repository for license details.

---

## 👤 Author

**extractive-mana-pulse** — [GitHub Profile](https://github.com/extractive-mana-pulse)
