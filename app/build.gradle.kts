plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.networktest"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.networktest"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packaging {
        resources.excludes.add("META-INF/*")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

}



dependencies {
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0") // Use the latest version
    implementation ("com.hbb20:ccp:2.7.0") // Use the latest version
    implementation("com.google.android.material:material:1.4.0") // Use the latest version
    implementation("com.android.volley:volley:1.2.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.springframework:spring-context:5.3.10")
    implementation ("org.springframework:spring-web:5.3.10")
    implementation ("com.airbnb.android:lottie:6.2.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.5")
    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testImplementation ("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("org.mockito:mockito-android:3.12.4")
    androidTestImplementation ("androidx.test.espresso:espresso-intents:3.4.0")
    testImplementation ("androidx.test.espresso:espresso-intents:3.4.0")
}
