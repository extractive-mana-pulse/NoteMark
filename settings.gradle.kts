pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NoteMark"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":auth")
include(":core")
include(":note")
include(":releases")
include(":releases:data")
include(":releases:domain")
include(":releases:presentation")
include(":note:data")
include(":note:domain")
include(":note:presentation")
include(":core:data")
include(":core:domain")
include(":core:presentation")
include(":auth:data")
include(":auth:domain")
include(":auth:presentation")