plugins {
    alias(libs.plugins.notemark.android.library.compose)
}

android {
    namespace = "com.example.presentation"
}

dependencies {
    implementation(projects.core.domain)
}