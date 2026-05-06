plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.tomosensei.core.ai"
    compileSdk = 34

    defaultConfig {
        minSdk = 31
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    sourceSets {
        getByName("main") { java.srcDirs("src/main/kotlin") }
    }
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.kotlinx.coroutines.android)

    // HTTP client used by ModelDownloader to stream the 3.6 GB Gemma 4
    // bundle with progress callbacks + SHA-256 verification.
    implementation(libs.okhttp)

    // MediaPipe Tasks GenAI — wraps the LiteRT-LM runtime that runs
    // Gemma 4 E4B on-device. Confirmed against the patterns in
    // google-ai-edge/gallery; the dep version may need to bump as
    // Google ships new Gemma support.
    implementation(libs.mediapipe.tasks.genai)
}
