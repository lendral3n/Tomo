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

    // LiteRT-LM / MediaPipe — surfaced through libs.versions.toml. The
    // actual inference call site is GemmaInferenceManager; until the
    // library is wired, the stub implementation in this module satisfies
    // the interface so :feature:chat can build and run end-to-end.
    // implementation(libs.google.ai.edge.litertlm)
    // implementation(libs.mediapipe.tasks.genai)
}
