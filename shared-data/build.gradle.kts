@file:Suppress("UnusedPrivateProperty")

import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.library)
    alias(libs.plugins.sqldelight)
    kotlin("multiplatform")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlinx.kover") version "0.7.5"
    id("com.codingfeline.buildkonfig")
}

buildkonfig {
    packageName = "com.foreverrafs.superdiary.buildKonfig"

    val openAiKey =
        project.findProperty("openAiKey") ?: throw Exception("OpenAI API key not provided!")

    // default config is required
    defaultConfigs {
        buildConfigField(STRING, "openAIKey", openAiKey.toString())
    }
}

sqldelight {
    databases.register("SuperDiaryDatabase") {
        packageName.set("com.foreverrafs.superdiary.database")
        deriveSchemaFromMigrations.set(true)
    }
}

kover {
    useJacoco("0.8.10")
}

koverReport {
    filters {
        excludes {
            packages("com.foreverrafs.superdiary.database", "db")
        }
    }
    defaults {
        // adds the contents of the reports of `release` Android build variant to default reports
        mergeWith("release")
    }
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    androidTarget()

    iosX64()
    jvm()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.coroutines.core)
                implementation(libs.kotlin.coroutines.native)
                implementation(libs.kotlin.datetime)
                implementation("co.touchlab:stately-common:2.0.6") // Because sqldelight is misbehaving
                implementation(libs.koin.core)
                implementation(libs.kotlin.inject.runtime)
                implementation(libs.square.sqldelight.coroutinesExt)
                implementation(platform("com.aallam.openai:openai-client-bom:3.6.1"))
                implementation("com.aallam.openai:openai-client")
                runtimeOnly(libs.ktor.client.cio)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.android)
                implementation(libs.square.sqldelight.coroutinesExt)
                implementation(libs.koin.android)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.junit)
                implementation(libs.koin.test)
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.turbine)
                implementation(libs.assertk.common)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.native)
                implementation(libs.kotlin.coroutines.core)
                implementation(libs.ktor.client.darwin)
                implementation(libs.ktor.client.ios)
            }
        }

        jvmMain {
            dependencies {
                implementation(libs.square.sqldelight.driver.sqlite)
            }
        }
    }
}

android {
    namespace = "com.foreverrafs.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minimumSdk.get().toInt()
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
}
