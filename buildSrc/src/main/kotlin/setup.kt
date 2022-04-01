import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.gradle.kotlin.dsl.*
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.android.build.gradle.LibraryExtension
import org.gradle.api.tasks.testing.logging.TestLogEvent

fun Project.configure() {
    extensions.configure<KotlinMultiplatformExtension> {
        jvm {
            compilations.all {
                kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
            }
            testRuns["test"].executionTask.configure {
                useJUnitPlatform()
                testLogging {
                    events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
                }

            }
        }
        android {
            publishAllLibraryVariants()
        }
    }

//   TODO:
//    js(BOTH) {
//        browser {
//            commonWebpackConfig {
//                cssSupport.enabled = true
//            }
//        }
//    }
//    val hostOs = System.getProperty("os.name")
//    val isMingwX64 = hostOs.startsWith("Windows")
//    val nativeTarget = when {
//        hostOs == "Mac OS X" -> macosX64("native")
//        hostOs == "Linux" -> linuxX64("native")
//        isMingwX64 -> mingwX64("native")
//        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
//    }

    findAndroidExtension().apply {
        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
}

private fun Project.findAndroidExtension(): BaseExtension = extensions.findByType<LibraryExtension>()
    ?: extensions.findByType<com.android.build.gradle.AppExtension>()
    ?: error("Could not found Android application or library plugin applied on module $name")
