import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.foreverrafs.superdiary.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    compileOnly(libs.conventionplugins.kover)
    compileOnly(libs.conventionplugins.ktlint)
    compileOnly(libs.conventionplugins.sonar)
    compileOnly("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:0.15.2")
    compileOnly("com.codingfeline.buildkonfig:buildkonfig-compiler:0.15.2")
}

gradlePlugin {
    val rootPackageName = "com.superdiary.gradle"

    plugins {

        register("GitHooksConventionPlugin") {
            id = "com.superdiary.githooks"
            implementationClass = "$rootPackageName.codequality.GitHooksConventionPlugin"
        }

        register("KotlinAndroidConventionPlugin") {
            id = "com.superdiary.kotlin.android"
            implementationClass = "$rootPackageName.kotlinjava.KotlinAndroidConventionPlugin"
        }

        register("KtlintConventionPlugin") {
            id = "com.superdiary.ktlint"
            implementationClass = "$rootPackageName.codequality.KtlintConventionPlugin"
        }

        register("SecretsConventionPlugin") {
            id = "com.superdiary.secrets"
            implementationClass = "$rootPackageName.secrets.SecretsConventionPlugin"
        }
    }
}
