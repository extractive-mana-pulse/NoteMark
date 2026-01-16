import com.android.build.api.dsl.LibraryExtension
import com.example.convention.configureAndroidCompose
import com.example.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply(libs.findPlugin("notemark.android.library").get().get().pluginId)
                apply(libs.findPlugin("compose.compiler").get().get().pluginId)
            }

            val extension = extensions.getByType<LibraryExtension>()
            configureAndroidCompose(extension)
        }
    }
}