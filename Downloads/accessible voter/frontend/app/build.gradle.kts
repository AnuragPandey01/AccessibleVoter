plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "xyz.droidev.eventsync"
    compileSdk = 34

    defaultConfig {
        applicationId = "xyz.droidev.eventsync"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation (libs.glide)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.android.compiler)
    implementation(libs.kotlinx.serialization.json)

    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)

    implementation(libs.face.mesh.detection)

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    debugImplementation ("com.github.chuckerteam.chucker:library:4.0.0")
    releaseImplementation ("com.github.chuckerteam.chucker:library-no-op:4.0.0")
    implementation("com.google.zxing:core:3.5.3")

}