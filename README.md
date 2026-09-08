# GuessUp 🎴

GuessUp is a modern Android party guessing game built with **Kotlin** and **Jetpack Compose**.

Hold your phone against your forehead, let your friends describe the word on screen, and tilt the device to mark the answer as correct or skip it.

The app was created as a customizable, offline and ad-free alternative to traditional guessing party games.

## 📥 Download

Download the latest APK from the **Releases** section:

[Download latest release](https://github.com/TiagoC18/GuessUp/releases/latest)

> Android 8.0 (API 26) or newer is required.

---

## ✨ Features

- 🎴 Built-in categories
- ➕ Create custom categories
- ✏️ Edit existing categories
- 🗑️ Remove individual words
- 📝 Add multiple words at once
- 📱 Tilt-based gameplay using Android motion sensors
- ✅ Tilt in one direction for a correct answer
- ❌ Tilt in the opposite direction to skip
- 🎯 Neutral-position detection before showing the next word
- ⏱️ Round duration from 30 seconds to 2 minutes
- 🔢 Configurable pre-game countdown
- 📳 Haptic feedback
- 🔊 Sound feedback
- 🚨 Pulsating final 10-second warning
- 🌙 Light, dark and system themes
- 📊 Animated round statistics and results
- 🎮 Fullscreen landscape gameplay
- 💾 Categories and settings stored locally
- 🚫 No ads
- 🌐 No internet connection required for gameplay

---

## 🎮 How to Play

1. Choose a category.
2. Select the duration of the round.
3. Place the phone horizontally against your forehead.
4. Wait for the countdown.
5. Let your friends describe the word shown on screen.
6. Tilt the phone in one direction when you guess correctly.
7. Tilt it in the opposite direction to skip.
8. Return the phone to the neutral position to reveal the next word.
9. Get as many correct answers as possible before the timer reaches zero.

---

## 🛠️ Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Android SensorManager
- Rotation Vector Sensor
- Kotlin StateFlow
- SharedPreferences
- Compose Animations

---

## 📱 Requirements

- Android 8.0 or newer
- Device with motion sensors
- Physical Android device recommended

Tilt-based gameplay is intended for a physical device and is not fully representative when tested through an emulator.

---

## 🏗️ Project Structure

```text
com.example.guessup
│
├── data
│   ├── CategoryRepository
│   ├── SettingsRepository
│   └── WordParser
│
├── model
│   ├── AppSettings
│   ├── Category
│   └── GameResult
│
├── navigation
│   └── GuessUpNavigation
│
├── screens
│   ├── HomeScreen
│   ├── CategoryScreen
│   ├── CategoryEditorScreen
│   ├── DurationScreen
│   ├── GameScreen
│   ├── ResultsScreen
│   └── SettingsScreen
│
├── sensor
│   └── TiltDetector
│
└── ui.theme
