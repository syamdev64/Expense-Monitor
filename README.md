# Expense Monitor 📊

Expense Monitor is a modern, full-featured Android application designed to help users track their daily expenses, manage budgets, and gain insights into their spending habits. Built with the latest Android development best practices, it offers a secure and seamless experience across local and cloud environments.

## ✨ Features

### 🔐 Security & Authentication
- **User Authentication**: Secure sign-up and login powered by Firebase Authentication.
- **Biometric Lock**: Added layer of security using Fingerprint or Face ID via the `BiometricPrompt` API.
- **Secure MPIN**: Set up a 4-digit MPIN for quick and secure access to your financial data.
- **Session Persistence**: "Remember Me" functionality to keep you logged in securely.

### 💰 Expense Management
- **Daily Tracking**: Easily add, edit, and delete expenses with categories and descriptions.
- **Budgeting**: Set a monthly budget and monitor your progress with intuitive visual indicators.
- **Category-wise Tracking**: Organize spending into custom categories (Food, Transport, Shopping, etc.).
- **Interactive Dashboard**: Real-time overview of total spent, remaining budget, and budget usage percentage.

### 📈 Data & Insights
- **Spend Analyzer**: Visualize your spending trends with interactive Pie charts (Category breakdown), Bar charts (Weekly trends), and Line charts (Monthly trends).
- **Date-based Filtering**: View expenses for Today, This Week, or This Month.
- **Search**: Quickly find specific expenses using the search functionality.

### ☁️ Cloud & Persistence
- **Offline First**: Uses Room Database for fast, local data access even without an internet connection.
- **Cloud Sync**: Automatic synchronization with Firebase Firestore ensures your data is backed up and accessible across devices.
- **Profile Management**: Update your display name, profile picture (Firebase Storage), and contact details.

---

## 🏗️ Architecture

The project follows the **Clean Architecture** principles combined with the **MVVM (Model-View-ViewModel)** pattern to ensure a highly modular, testable, and maintainable codebase.

### Layers:
1.  **UI Layer (Jetpack Compose)**: Declarative UI components that react to state changes.
2.  **ViewModel Layer**: Manages UI state and communicates with the Repository.
3.  **Repository Layer**: The single source of truth that abstracts data sources (Local Room DB vs. Remote Firebase).
4.  **Data Layer**:
    *   **Local**: Room Database for expense persistence and DataStore for user preferences.
    *   **Remote**: Firebase (Auth, Firestore, Storage) for cloud services.

---

## 🛠️ Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (100% Declarative UI)
- **Dependency Injection**: [Dagger Hilt](https://dagger.dev/hilt/)
- **Local Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Cloud Infrastructure**: [Firebase](https://firebase.google.com/) (Auth, Firestore, Storage, Analytics)
- **Navigation**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
- **Security**: Biometric API, DataStore Preferences
- **Concurrency**: Kotlin Coroutines & Flow

---

## 🚀 Getting Started

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/your-username/ExpenseMonitor.git
    ```
2.  **Firebase Setup**:
    - Create a new project in the [Firebase Console](https://console.firebase.google.com/).
    - Add an Android app with the package name `com.example.expensemonitor`.
    - Download the `google-services.json` file and place it in the `app/` directory.
    - Enable **Email/Password Authentication**, **Cloud Firestore**, and **Firebase Storage**.
3.  **Build & Run**:
    - Open the project in **Android Studio Ladybug** (or later).
    - Sync the Gradle project and run it on an emulator or physical device.

---

## 📸 Screenshots

| Dashboard | Reports | Profile |
| :---: | :---: | :---: |
| ![Dashboard](https://via.placeholder.com/200x400?text=Dashboard) | ![Reports](https://via.placeholder.com/200x400?text=Reports) | ![Profile](https://via.placeholder.com/200x400?text=Profile) |

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
=======
Expense Monitor 📊
--------------------
Expense Monitor is a modern, full-featured Android application designed to help users track their daily expenses, manage budgets, and gain insights into their spending habits. Built with the latest Android development best practices, it offers a secure and seamless experience across local and cloud environments.

✨ Features
===============

🔐 Security & Authentication

•User Authentication: Secure sign-up and login powered by Firebase Authentication.
•Biometric Lock: Added layer of security using Fingerprint or Face ID via the BiometricPrompt API.
•Secure MPIN: Set up a 4-digit MPIN for quick and secure access to your financial data.
•Session Persistence: "Remember Me" functionality to keep you logged in securely.

💰 Expense Management

•Daily Tracking: Easily add, edit, and delete expenses with categories and descriptions.
•Budgeting: Set a monthly budget and monitor your progress with intuitive visual indicators.
•Category-wise Tracking: Organize spending into custom categories (Food, Transport, Shopping, etc.).
•Interactive Dashboard: Real-time overview of total spent, remaining budget, and budget usage percentage.
📈 Data & Insights

•Spend Analyzer: Visualize your spending trends with interactive Pie charts (Category breakdown), Bar charts (Weekly trends), and Line charts (Monthly trends).
•Date-based Filtering: View expenses for Today, This Week, or This Month.
•Search: Quickly find specific expenses using the search functionality.
☁️ Cloud & Persistence

•Offline First: Uses Room Database for fast, local data access even without an internet connection.
•Cloud Sync: Automatic synchronization with Firebase Firestore ensures your data is backed up and accessible across devices.
•Profile Management: Update your display name, profile picture (Firebase Storage), and contact details.

🏗️ Architecture
-------------------

The project follows the Clean Architecture principles combined with the MVVM (Model-View-ViewModel) pattern to ensure a highly modular, testable, and maintainable codebase.
Layers:

1.UI Layer (Jetpack Compose): Declarative UI components that react to state changes.
2.ViewModel Layer: Manages UI state and communicates with the Repository.
3.Repository Layer: The single source of truth that abstracts data sources (Local Room DB vs. Remote Firebase).
4.Data Layer:
◦Local: Room Database for expense persistence and DataStore for user preferences.
◦Remote: Firebase (Auth, Firestore, Storage) for cloud services.

🛠️ Tech Stack
---------------
•Language: Kotlin
•UI: Jetpack Compose (100% Declarative UI)
•Dependency Injection: Dagger Hilt
•Local Database: Room
•Cloud Infrastructure: Firebase (Auth, Firestore, Storage, Analytics)
•Navigation: Navigation Compose
•Image Loading: Coil
•Security: Biometric API, DataStore Preferences
•Concurrency: Kotlin Coroutines & Flow

🚀 Getting Started
---------------------
1.Clone the repository:git clone https://github.com/your-username/ExpenseMonitor.git
2.Firebase Setup:

◦Create a new project in the Firebase Console.
◦Add an Android app with the package name com.example.expensemonitor.
◦Download the google-services.json file and place it in the app/ directory.
◦Enable Email/Password Authentication, Cloud Firestore, and Firebase Storage.
3.Build & Run:
◦Open the project in Android Studio Ladybug (or later).
◦Sync the Gradle project and run it on an emulator or physical device
