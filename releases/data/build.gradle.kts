plugins {
    alias(libs.plugins.notemark.android.library)
}

android {
    namespace = "com.example.data"
}

dependencies {
    implementation(projects.releases.domain)
}