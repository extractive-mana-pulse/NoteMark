plugins {
    alias(libs.plugins.notemark.android.library)
    alias(libs.plugins.ktor.client.convention)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)

}

android {
    namespace = "com.example.notemark.note.data"
}

dependencies {
    implementation(projects.note.domain)
    implementation(libs.bundles.room)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(projects.core.domain)
}