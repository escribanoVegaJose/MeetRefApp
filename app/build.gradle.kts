plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.indra.iscs.meetrefapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.indra.iscs.meetrefapp"
        minSdk = 27
        compileSdk = 34
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
}

dependencies {
    implementation("org.igniterealtime.smack:smack-android:4.4.7")
    {
        exclude(mapOf("group" to "xpp3", "module" to "xpp3"))
        exclude(mapOf("group" to "xpp3", "module" to "xpp3_min"))
    }
    implementation("org.igniterealtime.smack:smack-tcp:4.4.7") {
        exclude(mapOf("group" to "xpp3", "module" to "xpp3"))
        exclude(mapOf("group" to "xpp3", "module" to "xpp3_min"))
    }
    implementation("org.igniterealtime.smack:smack-bosh:4.4.7") {
        exclude(mapOf("group" to "org.apache.httpcomponents", "module" to "httpclient"))
        exclude(mapOf("group" to "org.apache.httpcomponents", "module" to "httpcore"))
    }
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation ("com.google.code.gson:gson:2.10")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}