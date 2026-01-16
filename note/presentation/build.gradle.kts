plugins {
    alias(libs.plugins.notemark.android.library.compose)
}

android {
    namespace = "com.example.notemark.note.presentation"
}

dependencies {
    implementation(libs.androidx.paging.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.lottie.compose)
    with(projects) {
        implementation(note.domain)
        implementation(core.domain)
        implementation(core.presentation)
        implementation(auth.presentation)
    }
}