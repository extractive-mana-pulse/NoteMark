import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class KtorClientConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // 1. Apply the serialization plugin required by Ktor for JSON processing
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            // 2. Add Ktor libraries as dependencies
            dependencies {
                "implementation"(libs.findLibrary("ktor-client-auth").get())
                "implementation"(libs.findLibrary("ktor-client-core").get())
                "implementation"(libs.findLibrary("ktor-client-android").get())
                "implementation"(libs.findLibrary("ktor-client-cio").get())
                "implementation"(libs.findLibrary("ktor-client-content-negotiation").get())
                "implementation"(libs.findLibrary("ktor-serialization").get())
                "implementation"(libs.findLibrary("ktor-client-logging").get())
            }
        }
    }
}
