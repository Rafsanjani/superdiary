@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://jitpack.io")
        mavenLocal()
    }
    includeBuild("build-logic")
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        mavenLocal()
    }

    versionCatalogs {
        create("libs") {
            from("io.github.rafsanjani:versions:2025.02.16")
            version("gradle", "8.8.0-alpha05")
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
    id("org.jetbrains.kotlinx.kover.aggregation") version "0.9.1"
}

kover {
    enableCoverage()

    val classes = listOf(
        "**/*Preview*",
        // Compose screens
        "**/*Tab*",
        // Koin modules and all that shit
        "**/di/*",
        // iOS view controller
        "**/ui/ViewController*",
        // The entry-point UI of the app
        "**/AppKt",
        // The android application class
        "**/DiaryApp.kt",
        // JVM App entrypoint
        "**/Main.kt",
        // SqlDelight database driver
        "**/*DatabaseDriver*",
        // Sqlite Database file
        "**/*Database",
        "**/*Resources*",
        "*.*.*ComposableSingletons*",
        "**/app/**",
        "**/androidApp/**",
        "**/DiaryListActions",
        "*Tab*",
        "*App*",
        "*AndroidPreviews*",
        "*BackPressHandler*",
        "*SnapshotTheme*",
        "*BottomNavigationScreen*",
        "*BottomNavigationRoute*",
        "*Dto*",
        "*DiaryListActions*",
        "*DiaryFilters*",
        "*DiarySortCriteria*",
        "*BuildKonfig*",
        "*DatabaseDriver*",
        "Modules*",
        "*Theme*",
        "*DiaryFilterSheet*",
        "*DiaryHeader*",
        "*DiarySearchBar*",
        "*DiarySelectionModifierBar*",
        "*AndroidDataStorePathResolver",
        "*.*NavType",
        "*.RemoteDataSource*",
        "db.*",
        "*di.*",
        "*screen.*",
        "*components.*",
        "*utils.FileSystem_androidKt",
        "*generated.resources.*",
        "*components.*",
    )

    reports {
        includedProjects.add(":shared-data")
        includedProjects.add(":shared-ui")

        excludedClasses.addAll(classes)
        excludesAnnotatedBy.add("androidx.compose.runtime.Composable")

        verify {
            rule {
                name = "Minimum Coverage Rule"
                bound {
                    minValue = 92
                }
            }
        }
    }
}

rootProject.name = "superdiary"
include(":androidApp:app")
include(":design-system")
include(":androidApp:benchmark")
include(":shared-data")
include(":swipe")
include(":shared-ui")
include(":core:authentication")
include(":core:analytics")
include(":core:location")
include(":core:logging")
include(":common-utils")
include(":common-test")
include(":core:secrets")
include(":core:database")
include(":core:database-test")
include(":desktopApp")
include(":feature:diary-ai")
include(":feature:diary-profile")
include(":feature:diary-auth")
include(":feature:diary-list")
include(":preferences:annotation")
include(":preferences:processor")
