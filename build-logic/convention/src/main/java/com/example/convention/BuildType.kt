package com.example.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.util.Properties

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    extensionType: ExtensionType
) {

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    localProperties.load(localPropertiesFile.inputStream())

    commonExtension.run {
        buildFeatures {
            buildConfig = true
            compose = true
        }

        when (extensionType) {
            ExtensionType.APPLICATION -> {
                extensions.configure<ApplicationExtension> {
                    buildTypes {
                        debug {
                            buildConfigField ("String", "BASE_URL", "\"https://notemark.pl-coding.com/\"")
                            isMinifyEnabled = false
                            proguardFiles(
                                getDefaultProguardFile("proguard-android-optimize.txt"),
                                "proguard-rules.pro"
                            )
                        }
                        release {
                            buildConfigField ("String", "BASE_URL", "\"https://notemark.pl-coding.com/\"")
                            isMinifyEnabled = true
                            proguardFiles(
                                getDefaultProguardFile("proguard-android-optimize.txt"),
                                "proguard-rules.pro"
                            )
                        }
                    }
                }
            }

            ExtensionType.LIBRARY -> {
                extensions.configure<LibraryExtension> {
                    buildTypes {
                        debug {
                            buildConfigField ("String", "BASE_URL", "\"https://notemark.pl-coding.com/\"")
                            isMinifyEnabled = false
                            proguardFiles(
                                getDefaultProguardFile("proguard-android-optimize.txt"),
                                "proguard-rules.pro"
                            )
                        }
                        release {
                            buildConfigField ("String", "BASE_URL", "\"https://notemark.pl-coding.com/\"")
                            isMinifyEnabled = true
                            proguardFiles(
                                getDefaultProguardFile("proguard-android-optimize.txt"),
                                "proguard-rules.pro"
                            )
                        }
                    }
                }
            }
        }
    }
}
