plugins {
    id("com.android.application") version "8.0.0-alpha08" apply false
    id("org.jetbrains.kotlin.android") version "1.7.21" apply false
    id("com.google.dagger.hilt.android") version "2.43.2" apply false
    id("org.jetbrains.kotlin.kapt") version "1.7.21" apply false
    id("com.google.devtools.ksp") version "1.7.21-1.0.8" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.20" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}