plugins {
    alias(libs.plugins.notemark.android.library)
    alias(libs.plugins.ktor.client.convention)
    alias(libs.plugins.compose.compiler)
    id("com.google.dagger.hilt.android")  // ✅ ADD THIS
    alias(libs.plugins.ksp)                // ✅ ADD THIS
}

android {
    namespace = "com.example.notemark.auth.data"
}

dependencies {
    // ✅ ADD HILT DEPENDENCIES
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    with(projects) {
        implementation(auth.domain)
        implementation(core.data)
        implementation(core.domain)
    }
}