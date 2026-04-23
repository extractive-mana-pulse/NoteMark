plugins {
    alias(libs.plugins.notemark.android.application)
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.notemark"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    // --- Android Core & Lifecycle ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.core.splashscreen)

    // --- Compose UI Layer ---
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material3.adaptive)
    implementation(libs.lottie.compose)

    // --- Internal Modules (Modularized Architecture) ---
    // Auth
    implementation(projects.auth.data)
    implementation(projects.auth.domain)
    implementation(projects.auth.presentation)
    // Core
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.presentation)
    // Note
    implementation(projects.note.data)
    implementation(projects.note.domain)
    implementation(projects.note.presentation)
    // Releases
    implementation(projects.releases.data)
    implementation(projects.releases.domain)
    implementation(projects.releases.presentation)

    // --- Navigation ---
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // --- Dependency Injection (Hilt) ---
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)
    ksp(libs.hilt.compiler)

    // --- Networking (Ktor) ---
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization)

    // --- Paging & Persistence ---
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.room.paging)

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    with(projects) {
        with(auth) {
            implementation(data)
            implementation(domain)
            implementation(presentation)
        }
        with(core) {
            implementation(data)
            implementation(domain)
            implementation(presentation)
        }
        with(note) {
            implementation(data)
            implementation(domain)
            implementation(presentation)
        }
        with(releases) {
            implementation(data)
            implementation(domain)
            implementation(presentation)
        }
    }
}