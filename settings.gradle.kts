pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "skylark-reader"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
