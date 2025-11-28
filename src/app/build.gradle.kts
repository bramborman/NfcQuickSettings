import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    compileSdk = 36
    compileSdkMinor = 1

    defaultConfig {
        applicationId = "com.bramborman.nfcquicksettings"
        minSdk = 24
        targetSdk = this@android.compileSdk
        versionCode = 4
        versionName = "1.3"
    }

    namespace = defaultConfig.applicationId

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
}
