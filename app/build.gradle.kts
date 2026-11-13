import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")

}
//API KEY
val localProps = Properties().apply{
    rootProject.file("local.properties")
        .takeIf { it.exists() }
        ?.inputStream()
        ?.use(::load)
}
android {
    namespace = "com.example.weatherdiscussion"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.weatherdiscussion"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "OPENWEATHER_API_KEY", "\"${localProps.getProperty("OPENWEATHER_API_KEY","")}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    //COMPOSE
    implementation(libs.androidx.navigation.compose)

    //NETWORKING
    implementation(libs.retrofit)
    implementation(libs.converter.kotlinx.serialization)
    implementation(libs.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)

    //COROUTINES AND LIFECYCLE
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    //IMAGE LOADERS
    implementation(libs.compose)
    implementation(libs.okhttp3.integration)
    ksp("com.github.bumptech.glide:ksp:4.16.0")
}
