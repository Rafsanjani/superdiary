package com.superdiary.gradle.codequality

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jlleitschuh.gradle.ktlint.KtlintExtension

class KtlintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            subprojects {
                pluginManager.apply("org.jlleitschuh.gradle.ktlint")

                ktlint {
                    version.set("1.5.0")

                    filter {
                        exclude { it.file.path.contains("${layout.buildDirectory.get()}") }
                    }
                }

                dependencies {
                    add("ktlintRuleset", target.versionCatalog("ktlint.rules.compose"))
                }
            }
        }
    }
}

private fun Project.versionCatalog(alias: String): Provider<MinimalExternalModuleDependency> {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    return libs.findLibrary(alias).get()
}

private fun Project.ktlint(action: KtlintExtension.() -> Unit) =
    extensions.configure<KtlintExtension>(action)
