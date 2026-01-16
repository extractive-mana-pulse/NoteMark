plugins {
    alias(libs.plugins.notemark.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ktor.client.convention)
}

android {
    namespace = "com.example.notemark.core.data"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(projects.core.domain)
    implementation(projects.auth.domain)
}