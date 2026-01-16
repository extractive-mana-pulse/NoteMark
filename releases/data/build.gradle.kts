plugins {
    alias(libs.plugins.notemark.android.library)
    alias(libs.plugins.ktor.client.convention)
    alias(libs.plugins.compose.compiler)

}

android {
    namespace = "com.example.notemark.releases.data"
}

dependencies {

    with(projects){
        implementation(core.domain)
        implementation(releases.domain)
    }
}