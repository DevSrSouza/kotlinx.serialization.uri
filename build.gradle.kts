plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.mavenPublish) apply false
}

val linter = libs.plugins.ktlint.get().pluginId
val mavenPublish = libs.plugins.mavenPublish.get().pluginId

group = "br.com.devsrsouza.kotlinx"
version = "0.1.0-SNAPSHOT"

subprojects {
    plugins.apply(linter)
    plugins.apply(mavenPublish)
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
