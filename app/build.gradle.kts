plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

fun String.asBuildConfigString(): String =
    "\"${replace("\\", "\\\\").replace("\"", "\\\"")}\""

val apiBaseUrl = providers.gradleProperty("PARKING_API_BASE_URL")
    .orElse(providers.environmentVariable("PARKING_API_BASE_URL"))

android {
    namespace = "io.github.javierquinan.parking"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.javierquinan.parking"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            val debugBaseUrl = apiBaseUrl
                .orElse("http://10.0.2.2/Parcial/")
                .get()
            buildConfigField("String", "API_BASE_URL", debugBaseUrl.asBuildConfigString())
        }

        release {
            isMinifyEnabled = false
            val releaseBaseUrl = apiBaseUrl.orElse("").get()
            buildConfigField("String", "API_BASE_URL", releaseBaseUrl.asBuildConfigString())
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
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.android.volley:volley:1.2.1")
    implementation("androidx.activity:activity:1.9.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}
