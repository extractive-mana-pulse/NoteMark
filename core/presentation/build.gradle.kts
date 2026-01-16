plugins {
    alias(libs.plugins.notemark.android.library.compose)
}

android {
    namespace = "com.example.notemark.core.presentation"
}

dependencies {
    implementation(projects.core.domain)
}