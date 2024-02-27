plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "hello.kwfriends"
    compileSdk = 34

    defaultConfig {
        applicationId = "hello.kwfriends"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

val composeUiVersion = "1.6.0"
val navVersion = "2.7.6"

dependencies {// mode
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

//  disabled for: https://issuetracker.google.com/issues/322214617
//  implementation("androidx.compose.material3:material3:1.1.2")

    implementation("androidx.compose.material3:material3:1.2.0-rc01")
    implementation("androidx.compose.ui:ui:$composeUiVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeUiVersion")
    implementation("androidx.compose.material:material:1.6.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeUiVersion")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    debugImplementation("androidx.compose.ui:ui-tooling:$composeUiVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeUiVersion")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion")
    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeUiVersion")
    implementation("androidx.compose.ui:ui-util:$composeUiVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0") //preference datastore
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    // Add the dependency for the Cloud Storage library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-storage")
    implementation("io.coil-kt:coil-compose:2.5.0") //for Coil
    implementation("androidx.core:core-splashscreen:1.0.1") //for splash screen
    implementation("com.google.firebase:firebase-database") //firebase database

    // shimmer effect
    implementation("com.valentinilk.shimmer:compose-shimmer:1.2.0")

    implementation ("com.google.firebase:firebase-messaging-ktx") //firebase messaging
    implementation ("com.google.firebase:firebase-analytics-ktx") //firebase analytics
}