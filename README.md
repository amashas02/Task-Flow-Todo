# 📋 TaskFlow — Android Task Management App

> A modern, beautifully designed task management application built with Java, Room Database, and Material Design components.

---

## 📸 Features

### ✅ Task Management
- **Create tasks** with title, description, and tags
- **Edit tasks** via an intuitive dialog with pre-filled fields
- **Delete tasks** with a confirmation prompt
- **Toggle completion** — tap the check icon to mark tasks as done (strikethrough styling)
- **Filter tasks** using All / Active / Done tabs
- **Progress tracking** — visual progress card showing completed percentage

### 👤 User Profile
- **User authentication** — secure registration & login with SHA-256 password hashing
- **Avatar selection** — choose between male and female avatars
- **Personal info** — first name, last name, date of birth (date picker), and gender
- **Task statistics** — total, active, and completed task counts displayed on the profile page

### 🔔 Notifications
- System notifications when a task is created or completed
- Android 13+ runtime permission handling for `POST_NOTIFICATIONS`

### 🎨 Design & Branding
- **2-second branded splash screen** with fade-in animation
- **Custom TaskFlow logo** — vector-based adaptive icon
- **Blue-themed color palette** matching the brand identity
- **Material Design** components throughout (cards, tabs, buttons)
- **Notch/status bar safe** — `fitsSystemWindows` enabled

---

## 🏗️ Architecture

```
com.taskflow.todo/
├── activity/
│   ├── SplashActivity.java        # 2s splash screen with session routing
│   ├── LoginActivity.java         # User login with SHA-256 auth
│   ├── RegisterActivity.java      # New user registration
│   └── MainActivity.java          # Bottom nav host (show/hide pattern)
│
├── fragment/
│   ├── HomeFragment.java          # Task list, filters, progress, edit/delete
│   ├── AddTaskFragment.java       # New task creation form
│   ├── ProfileFragment.java       # User profile display & stats
│   └── EditProfileFragment.java   # Avatar, name, DOB, gender editor
│
├── adapter/
│   └── TaskAdapter.java           # RecyclerView adapter with DiffUtil
│
├── data/
│   ├── entity/
│   │   ├── User.java              # User entity (Room)
│   │   └── Todo.java              # Todo entity (Room)
│   ├── dao/
│   │   ├── UserDao.java           # User data access queries
│   │   └── TodoDao.java           # Todo data access queries
│   └── db/
│       └── TodoDatabase.java      # Room database singleton
│
└── util/
    ├── SessionManager.java        # SharedPreferences session handling
    ├── CryptoUtil.java            # SHA-256 password hashing
    └── NotificationHelper.java    # Notification channel & dispatch
```

---

## 🛠️ Tech Stack

| Component | Technology |
|---|---|
| **Language** | Java |
| **Min SDK** | 26 (Android 8.0) |
| **Target SDK** | 35 (Android 15) |
| **Database** | Room Persistence Library 2.6.1 |
| **UI Components** | Material Components 1.12.0 |
| **Layout** | ConstraintLayout, LinearLayout, ScrollView |
| **List Rendering** | RecyclerView with DiffUtil |
| **Auth** | SHA-256 hashing via `MessageDigest` |
| **Session** | SharedPreferences |
| **Notifications** | NotificationCompat + Channels |

---

## 📦 Dependencies

```kotlin
implementation("androidx.appcompat:appcompat:1.7.0")
implementation("com.google.android.material:material:1.12.0")
implementation("androidx.constraintlayout:constraintlayout:2.2.1")
implementation("androidx.recyclerview:recyclerview:1.3.2")
implementation("androidx.core:core:1.13.1")
implementation("androidx.room:room-runtime:2.6.1")
annotationProcessor("androidx.room:room-compiler:2.6.1")
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** Hedgehog (2023.1) or newer
- **JDK 8+**
- **Android SDK 35**

### Build & Run

1. Clone or download the project
2. Open in Android Studio
3. Sync Gradle
4. Run on an emulator or physical device (API 26+)

```bash
# Command-line build
./gradlew assembleDebug

# Install to connected device
./gradlew installDebug
```

---

## 🎨 Color Palette

| Name | Hex | Usage |
|---|---|---|
| Primary | `#2488B7` | Buttons, active states, accents |
| Primary Light | `#EAF4F9` | Light backgrounds, selection highlights |
| Text Dark | `#163A75` | Headings, primary text |
| Text Muted | `#9AB0C2` | Secondary text, placeholders |
| Background | `#FFFFFF` | Page backgrounds |
| Secondary BG | `#F3F5F8` | Cards, avatar backgrounds |
| Input Border | `#DFE3EB` | Form field borders |
| Error | `#D32F2F` | Error messages, delete actions |

---

## 📂 Database Schema

### `users` table
| Column | Type | Notes |
|---|---|---|
| `id` | `LONG` | Primary key, auto-generated |
| `username` | `STRING` | Unique |
| `passwordHash` | `STRING` | SHA-256 hash |
| `firstName` | `STRING` | Optional |
| `lastName` | `STRING` | Optional |
| `dateOfBirth` | `STRING` | DD/MM/YYYY format |
| `gender` | `STRING` | "Male" or "Female" |
| `avatarType` | `STRING` | "male" or "female" |

### `todos` table
| Column | Type | Notes |
|---|---|---|
| `id` | `LONG` | Primary key, auto-generated |
| `userId` | `LONG` | Foreign key to users |
| `title` | `STRING` | Required |
| `description` | `STRING` | Optional |
| `tags` | `STRING` | Comma-separated |
| [isCompleted](file:///c:/Users/Admin/Downloads/todo-android-app/todo-android-app/app/src/main/java/com/taskflow/todo/data/entity/Todo.java#42-43) | `BOOLEAN` | Default: false |
| `createdAt` | `LONG` | Epoch milliseconds |

---
