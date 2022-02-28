plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}

val linter = libs.plugins.ktlint.get().pluginId

group = "br.com.devsrsouza.kotlinx"
version = "0.1.0-SNAPSHOT"

subprojects {
    plugins.apply(linter)
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
