plugins {
    alias(libs.plugins.notemark.android.library.compose)
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.notemark.auth.presentation"
}

dependencies {
    with(projects) {
        implementation(core.domain)
        implementation(auth.domain)
        implementation(auth.data)
        implementation(core.presentation)
    }

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
}