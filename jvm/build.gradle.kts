plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
}

dependencies {
    api(projects.core)
    implementation(libs.guava)
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}
