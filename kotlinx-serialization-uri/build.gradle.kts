plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    id(libs.plugins.android.library.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

configure()

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.serialization.core)
                implementation(libs.kotlin.serialization.json)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.guava)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.kotest.framework)
                //implementation(libs.kotest.assertions)
                implementation(libs.junit.api)
                runtimeOnly(libs.junit.engine)
            }
        }
    }
}

android {
    compileSdkVersion(31)
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
}
