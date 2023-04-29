pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jcenter.bintray.com")
    }
}

include(":app")
rootProject.name = "NoLitter"
