plugins {

    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.cinelog"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cinelog"
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


    viewBinding {
        enable = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation ("com.github.bumptech.glide:compose:1.0.0-beta01")
    implementation("com.google.firebase:firebase-firestore")
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.0")
    implementation ("androidx.paging:paging-runtime:3.2.1")
    implementation("com.google.firebase:firebase-storage")

    // 원형 그래프 라이브러리
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

}