# Tomo Sensei — Project Structure

Multi-module Gradle project dengan separation of concerns.

---

## Top-level structure

```
tomo-sensei/
├── app/                              # Main application module (UI shell, navigation)
├── feature/                          # Feature modules (UI + ViewModel per feature)
│   ├── onboarding/
│   ├── drill/
│   ├── chat/
│   ├── photo/
│   ├── stats/
│   ├── settings/
│   ├── gate-config/
│   └── emergency/
├── service/                          # Background services & system integration
│   ├── gate-engine/
│   ├── trigger-monitor/
│   ├── overlay/
│   ├── accessibility/
│   ├── timeout-lock/
│   ├── bubble/
│   ├── notif-gate/
│   └── audio-companion/
├── core/                             # Shared business logic & infrastructure
│   ├── srs/
│   ├── ai/
│   ├── content/
│   ├── data/
│   ├── design-system/
│   ├── tts-stt/
│   ├── permissions/
│   └── common/
├── testing/                          # Test utilities
│   ├── shared-test-utils/
│   └── fakes/
├── docs/                             # Documentation
│   ├── 00-README.md
│   ├── 01-SPEC.md
│   ├── 02-IMPLEMENTATION-GUIDE.md
│   ├── 03-PROJECT-STRUCTURE.md
│   ├── 04-CLAUDE-CODE-PROMPT.md
│   ├── 05-DEVELOPMENT-CHECKLIST.md
│   └── 06-CONTENT-STRATEGY.md
├── content/                          # Deck content (JSON files)
│   ├── decks/
│   │   ├── n5.json
│   │   ├── n4.json
│   │   └── n3.json
│   └── prompts/                      # AI prompt templates
│       ├── sensei-base.txt
│       ├── photo-sensei.txt
│       └── card-deep-dive.txt
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
│   └── libs.versions.toml
└── README.md
```

---

## Module dependencies (DAG)

```
app
 ├── feature:onboarding
 ├── feature:drill
 ├── feature:chat
 ├── feature:photo
 ├── feature:stats
 ├── feature:settings
 ├── feature:gate-config
 ├── feature:emergency
 ├── service:gate-engine
 ├── service:trigger-monitor
 └── service:overlay

feature:* depends on:
 ├── core:design-system
 ├── core:data
 ├── core:common
 └── (specific cores per feature)

service:* depends on:
 ├── core:srs
 ├── core:data
 ├── core:ai (only services that need AI)
 ├── core:permissions
 └── core:common

core:ai depends on:
 ├── core:common
 (LiteRT-LM dependencies)

core:data depends on:
 ├── core:common
 (Room dependencies)
```

**Rule:** core modules **tidak boleh** import dari feature/service modules.

---

## Inside each module

### Feature module example: `feature/drill`

```
feature/drill/
├── build.gradle.kts
└── src/main/
    └── kotlin/com/tomosensei/feature/drill/
        ├── ui/
        │   ├── DrillScreen.kt           # Compose composable
        │   ├── DrillCardComponent.kt
        │   └── components/
        │       ├── SwipeableCard.kt
        │       └── ResponseButtons.kt
        ├── viewmodel/
        │   └── DrillViewModel.kt
        ├── di/
        │   └── DrillModule.kt           # Hilt module
        └── DrillNavGraph.kt              # Navigation entry
```

### Service module example: `service/gate-engine`

```
service/gate-engine/
├── build.gradle.kts
└── src/main/
    └── kotlin/com/tomosensei/service/gateengine/
        ├── GateEngine.kt                 # Main class
        ├── model/
        │   ├── Gate.kt
        │   ├── Trigger.kt
        │   └── IntensityLevel.kt
        ├── cooldown/
        │   └── CooldownTracker.kt
        ├── launcher/
        │   └── GateLauncher.kt
        ├── logger/
        │   └── GateLogger.kt
        └── di/
            └── GateEngineModule.kt
```

### Core module example: `core/ai`

```
core/ai/
├── build.gradle.kts
└── src/main/
    └── kotlin/com/tomosensei/core/ai/
        ├── GemmaInferenceManager.kt
        ├── ModelDownloader.kt
        ├── PromptBuilder.kt
        ├── parser/
        │   ├── PhotoAnalysisParser.kt
        │   └── JsonResponseParser.kt
        ├── model/
        │   ├── PhotoAnalysis.kt
        │   ├── ChatRequest.kt
        │   └── InferenceResult.kt
        └── di/
            └── AIModule.kt
```

---

## Key files per module

### `app/`
- `TomoSenseiApp.kt` — Application class (@HiltAndroidApp)
- `MainActivity.kt` — Entry activity dengan NavHost
- `ui/MainNavGraph.kt` — Root navigation
- `AndroidManifest.xml` — Permissions, services, receivers

### `core/data/`
- `db/TomoSenseiDatabase.kt`
- `db/dao/CardDao.kt`, `ProgressDao.kt`, etc
- `db/entity/` — Room entities
- `repository/CardRepository.kt`
- `repository/ProgressRepository.kt`
- `repository/SettingsRepository.kt`
- `di/DataModule.kt`

### `core/srs/`
- `FsrsScheduler.kt` — main algorithm
- `model/FsrsCard.kt` — internal state model
- `model/FsrsParameters.kt` — w[0..16] weights
- `FsrsConverter.kt` — convert between FSRS and Room state

### `core/design-system/`
- `theme/Color.kt`, `Typography.kt`, `Theme.kt`
- `components/WashiCard.kt`, `HankoStamp.kt`, `SenseiButton.kt`
- `components/JapaneseText.kt` — Japanese-aware Text component
- `tokens/Spacing.kt`, `Radius.kt`

### `core/permissions/`
- `PermissionChecker.kt`
- `PermissionRequester.kt`
- `model/PermissionType.kt`

---

## Content folder

```
content/
├── decks/
│   ├── n5.json                       # ~800 vocab + 100 kanji + grammar
│   ├── n5-meta.json                  # version, checksum
│   ├── n4.json
│   └── ...
├── prompts/
│   ├── sensei-base.txt
│   ├── photo-sensei.txt
│   └── card-deep-dive.txt
└── audio/                            # opsional, kalau pre-record
    └── n5/
```

Format `n5.json`:
```json
{
  "version": "1.0",
  "level": "N5",
  "language": "ja",
  "interface_lang": "id",
  "cards": [
    {
      "id": "n5_v_001",
      "type": "vocab",
      "front": "食べる",
      "reading": "たべる",
      "meaning": "makan",
      "examples": [
        {
          "jp": "私はりんごを食べます。",
          "reading": "わたしはりんごをたべます。",
          "id": "Saya makan apel."
        }
      ],
      "tags": ["verb", "daily", "food"],
      "mnemonic": null
    }
  ]
}
```

Decks di-bundle di `app/src/main/assets/decks/` saat build, atau download dari backend pada first launch.

---

## AndroidManifest.xml structure (key parts)

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"
        tools:ignore="ProtectedPermissions" />
    <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />

    <application
        android:name=".TomoSenseiApp"
        android:label="@string/app_name"
        android:theme="@style/Theme.TomoSensei"
        ...>

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:launchMode="singleTask">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- Foreground service for gate engine -->
        <service
            android:name="com.tomosensei.service.gateengine.GateForegroundService"
            android:foregroundServiceType="specialUse"
            android:exported="false">
            <property
                android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE"
                android:value="user-initiated learning gates" />
        </service>

        <!-- Overlay service -->
        <service
            android:name="com.tomosensei.service.overlay.GateOverlayService"
            android:exported="false" />

        <!-- Boot receiver -->
        <receiver
            android:name=".receiver.BootReceiver"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>

        <!-- Unlock receiver -->
        <receiver
            android:name=".receiver.UnlockReceiver"
            android:exported="false">
            <intent-filter>
                <action android:name="android.intent.action.USER_PRESENT" />
            </intent-filter>
        </receiver>

        <!-- Accessibility service (V1.0+ only, for hardcore mode) -->
        <!-- <service
            android:name=".service.accessibility.GateAccessibilityService"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService" />
            </intent-filter>
            <meta-data
                android:name="android.accessibilityservice"
                android:resource="@xml/accessibility_service_config" />
        </service> -->

    </application>
</manifest>
```

---

## settings.gradle.kts

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TomoSensei"
include(":app")

// Features
include(":feature:onboarding")
include(":feature:drill")
include(":feature:chat")
include(":feature:photo")
include(":feature:stats")
include(":feature:settings")
include(":feature:gate-config")
include(":feature:emergency")

// Services
include(":service:gate-engine")
include(":service:trigger-monitor")
include(":service:overlay")
include(":service:bubble")
include(":service:notif-gate")
// include(":service:accessibility")  // V1.0+
// include(":service:timeout-lock")   // V1.0+
// include(":service:audio-companion") // V1.0+

// Core
include(":core:srs")
include(":core:ai")
include(":core:content")
include(":core:data")
include(":core:design-system")
include(":core:tts-stt")
include(":core:permissions")
include(":core:common")

// Testing
include(":testing:shared-test-utils")
include(":testing:fakes")
```

---

*Adjust seiring kebutuhan. Modules bisa dipecah lebih kecil kalau ada coupling, atau digabung kalau jarang berubah independent.*
