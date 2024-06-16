import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.kotlinx.kover")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
}

kotlin {
    jvm()
    sourceSets {
        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.material3)
                implementation(libs.koin.jvm)
                implementation(projects.core.analytics)
                implementation(projects.core.logging)
                implementation(projects.sharedUi)
                implementation(projects.sharedData)
            }
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xskip-prerelease-check",
        )
    }


}

compose.desktop {
    application {
        mainClass = "com.foreverrafs.superdiary.ApplicationKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Deb,
                TargetFormat.Msi,
                TargetFormat.Deb,
            )
            packageName = "Superdiary"
            packageVersion = "1.0.0"
        }
    }
}
