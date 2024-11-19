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
    implementation(libs.google.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")

    implementation (libs.cardview) // 카드뷰

    implementation("com.google.firebase:firebase-firestore:25.1.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-firestore") //파이어 베이스 라이브러리의 의존성 선언
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")

}