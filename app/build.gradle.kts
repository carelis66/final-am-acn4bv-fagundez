plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.misquehaceresapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.misquehaceresapp"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // Firebase Auth
    implementation("com.google.firebase:firebase-auth")

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics")

    // Firestore
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// 🔥 Tarea para obtener el SHA1 aunque no exista signingReport
tasks.register("printSha1") {
    doLast {
        val keystore = System.getProperty("user.home") + "/.android/debug.keystore"
        val cmd = "keytool -list -v -keystore $keystore -alias androiddebugkey -storepass android -keypass android"
        println("Ejecutando: $cmd")
        Runtime.getRuntime().exec(cmd).inputStream.bufferedReader().use {
            println(it.readText())
        }
    }
}
