plugins {
    id("com.android.application") version "8.3.0-alpha02" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10" apply false
}

true // Needed to make the Suppress annotation work for the plugins block