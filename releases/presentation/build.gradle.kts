plugins {
    alias(libs.plugins.notemark.android.library.compose)
}

android {
    namespace = "com.example.presentation"
}

dependencies {
    with(projects) {
        implementation(core.domain)
        implementation(releases.domain)
        implementation(core.presentation)
    }
}