plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.dagger.hilt)
    alias(libs.plugins.google.devtools)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "lantian.nolitter"
    compileSdk = 36
    buildFeatures.compose = true

    defaultConfig {
        applicationId = "lantian.nolitter"
        minSdk = 24
        targetSdk = 36
        versionCode = 24
        versionName = "1.6.0"
        androidResources.localeFilters += listOf("en", "zh")
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }
}

dependencies {
    // Kotlin
    compileOnly(libs.xposed.api)
    implementation(libs.core.ktx)

    // Jetpack Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.animation)
    implementation(libs.material3)
    implementation(libs.material.icons.extended)
    implementation(libs.activity.compose)

    // Lifecycle
    implementation(libs.navigation.compose)
    implementation(libs.accompanist.drawablepainter)

    // Storage
    implementation(libs.datastore.preferences)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Dagger Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Serialization
    implementation(libs.kotlinx.serialization.csv)
}
