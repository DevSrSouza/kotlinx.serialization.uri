plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    configure()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.serialization.core)
                implementation(libs.kotlin.serialization.json)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(projects.jvm)

                implementation(libs.kotest.framework)
                //implementation(libs.kotest.assertions)
                implementation(libs.junit.api)
                runtimeOnly(libs.junit.engine)
            }
        }
    }
}
