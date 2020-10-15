plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "at.connyduck.pixelcat"
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "0.0 snapshot1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("shrinker-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    androidExtensions {
        features = setOf("parcelize")
    }

    buildFeatures {
        viewBinding = true
    }

    packagingOptions {
        exclude("LICENSE_OFL")
        exclude("LICENSE_UNICODE")
        exclude("okhttp3/internal/publicsuffix/NOTICE")
        exclude("DebugProbesKt.bin")
    }

    sourceSets["main"].java.srcDir("src/main/kotlin")
    sourceSets["test"].java.srcDir("src/test/kotlin")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
    withType<Test> {
        useJUnitPlatform()
    }
}

dependencies {

    val lifecycleVersion = "2.3.0-alpha07"
    val emojiCompatVersion = "1.2.0-alpha01"
    val roomVersion = "2.3.0-alpha03"
    val okHttpVersion = "4.8.1"
    val retrofitVersion = "2.9.0"
    val moshiVersion = "1.10.0"
    val daggerVersion = "2.28.3"
    val jUnitVersion = "5.7.0"

    implementation(kotlin("stdlib-jdk7"))

    implementation("androidx.core:core-ktx:1.5.0-alpha03")
    implementation("androidx.appcompat:appcompat:1.3.0-alpha02")
    implementation("androidx.activity:activity-ktx:1.2.0-alpha08")
    implementation("androidx.fragment:fragment-ktx:1.3.0-alpha08")
    implementation("com.google.android.material:material:1.3.0-alpha02")
    implementation("androidx.constraintlayout:constraintlayout:2.0.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.2.0-alpha05")
    implementation("androidx.annotation:annotation:1.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
    implementation("androidx.preference:preference:1.1.1")
    implementation("androidx.emoji:emoji-bundled:$emojiCompatVersion")
    implementation("androidx.emoji:emoji-appcompat:$emojiCompatVersion")
    implementation("androidx.paging:paging-runtime-ktx:3.0.0-alpha06")
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")

    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")

    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    implementation("com.squareup.moshi:moshi-adapters:$moshiVersion")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")

    implementation("com.fxn769:pix:1.4.4")

    implementation("com.github.yalantis:ucrop:2.2.5")

    implementation("me.relex:circleindicator:2.1.4")

    implementation("io.coil-kt:coil:1.0.0-rc3")

    implementation("com.github.connyduck:sparkbutton:4.0.0")

    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    implementation("com.google.dagger:dagger-android-support:$daggerVersion")
    kapt("com.google.dagger:dagger-android-processor:$daggerVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")

    testImplementation("com.squareup.okhttp3:mockwebserver:$okHttpVersion")
}
