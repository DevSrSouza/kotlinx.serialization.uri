pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}


enableFeaturePreview("VERSION_CATALOGS")
//enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":kotlinx-serialization-uri")

// preventing extra Gradle Daemon
rootDir.resolve("gradle.properties").copyTo(
    target = rootDir.resolve("buildSrc").resolve("gradle.properties"),
    overwrite = true
)
