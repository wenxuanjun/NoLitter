plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    compileSdk = 33
    namespace = "lantian.nolitter"
    buildFeatures.compose = true
    kotlinOptions.jvmTarget = "11"
    composeOptions.kotlinCompilerExtensionVersion = "1.4.0-alpha02"

    defaultConfig {
        applicationId = "lantian.nolitter"
        minSdk = 24
        targetSdk = 33
        versionCode = 21
        versionName = "1.6.0"
        resourceConfigurations.addAll(listOf("en-rUS", "zh-rCN"))
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Kotlin
    compileOnly("de.robv.android.xposed:api:82")
    implementation("androidx.core:core-ktx:1.9.0")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.4.0-alpha03")
    implementation("androidx.compose.ui:ui-tooling:1.4.0-alpha03")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.0-alpha03")
    implementation("androidx.compose.animation:animation:1.4.0-alpha03")
    implementation("androidx.compose.material3:material3:1.1.0-alpha03")
    implementation("androidx.compose.material:material-icons-extended:1.4.0-alpha03")
    implementation("androidx.activity:activity-compose:1.6.1")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0-alpha03")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("com.google.accompanist:accompanist-drawablepainter:0.28.0")

    // Storage
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.room:room-ktx:2.4.3")
    ksp("androidx.room:room-compiler:2.4.3")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.44.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt("com.google.dagger:hilt-android-compiler:2.44.2")

    // Serialization
    implementation("app.softwork:kotlinx-serialization-csv:0.0.8")
}
