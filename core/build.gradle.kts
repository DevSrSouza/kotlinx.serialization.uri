plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
    google()
}

kotlin {
    configure()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.serialization.core)
                implementation(libs.kotlin.serialization.json) // TODO: remove
            }
        }
    }
}
