import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
}

/**
 * Resolves a build secret by name.
 *
 * Priority: gitignored `local.properties` first, then the committed
 * `local.defaults.properties` placeholder file. Returns an EMPTY STRING (not "null")
 * when the key is missing in both, so the generated `buildConfigField` string literal
 * stays valid Kotlin and CI/dev builds never fail on absent keys.
 */
fun secretKey(name: String): String {
    val props = Properties()
    val local = rootProject.file("local.properties")
    if (local.exists()) props.load(local.inputStream())
    return props.getProperty(name) ?: run {
        val p = Properties()
        val defaults = rootProject.file("local.defaults.properties")
        if (defaults.exists()) p.load(defaults.inputStream())
        p.getProperty(name) ?: ""
    }
}

android {
    namespace = "com.example.weatherforecast"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weatherforecast"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        // buildConfigField blocks - API keys are read via secretKey() from gitignored
        // local.properties (fallback: committed empty local.defaults.properties) and
        // are never committed to VCS; each field is read back through BuildConfig
        buildConfigField("String", "MAP_API_KEY", "\"${secretKey("MAP_API_KEY")}\"")
        buildConfigField("String", "OWM_API_KEY", "\"${secretKey("OWM_API_KEY")}\"")
        buildConfigField("String", "MAPTILER_API_KEY", "\"${secretKey("MAPTILER_API_KEY")}\"")
        buildConfigField("String", "IQAIR_API_KEY", "\"${secretKey("IQAIR_API_KEY")}\"")

        manifestPlaceholders["googleMapsKey"] = secretKey("googleMapsKey")
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_KEY", "\"${secretKey("API_KEY")}\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "API_KEY", "\"${secretKey("API_KEY")}\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        // isCoreLibraryDesugaringEnabled = true - required so java.time (java.time.LocalDate etc.)
        // works on minSdk 24 devices
        isCoreLibraryDesugaringEnabled = true
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        // composeOptions kotlinCompilerExtensionVersion must stay aligned with the Kotlin 2.0.21
        // Compose compiler plugin version declared at the root
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // testOptions block is intentionally commented out - unit tests run on JUnit 4
    // (testImplementation junit:junit), not the JUnit Platform
//    testOptions {
  //      unitTests.all { useJUnitPlatform() }
    //    }
}

dependencies {

    // ---------- Core ----------
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    // collectAsStateWithLifecycle() — used by all composables that read the
    // SettingsViewModel StateFlows (review item 5). Version aligned with the
    // existing lifecycle 2.8.4 group.
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")
    implementation("androidx.fragment:fragment-ktx:1.8.2")
    implementation("androidx.activity:activity-compose:1.9.1")

    // ---------- Compose ----------
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    // M2 composable artifact removed (only M3 composables are used now). The
    // shared Icons.* (Icons.Filled.Menu, Icons.AutoMirrored.Filled.ArrowBack)
    // live in the dedicated icons artifact, so it is added explicitly instead
    // of relying on the transitive dependency that used to come with material.
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.foundation:foundation")
    //implementation 'androidx.compose.foundation:foundation:1.10.3'// pager

debugImplementation("androidx.compose.ui:ui-tooling")
debugImplementation("androidx.compose.ui:ui-test-manifest")

    // ---------- Navigation ----------
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.0")

    // ---------- Hilt ----------
    implementation("com.google.dagger:hilt-android:2.57.2")
    ksp("com.google.dagger:hilt-compiler:2.57.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // ---------- Retrofit + OkHttp ----------
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ---------- Coroutines ----------
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // ---------- Room ----------
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // ---------- Coil ----------
    implementation("io.coil-kt:coil-compose:2.6.0")

    // ---------- MapLibre ----------
    implementation("org.maplibre.gl:android-sdk:10.2.0")
    implementation("org.maplibre.gl:android-plugin-annotation-v9:3.0.2")

    // ---------- Location + Maps ----------
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:maps-compose:6.1.0")

    // ---------- DataStore ----------
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // ---------- Desugaring ----------
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // ---------- Tests ----------
    testImplementation("junit:junit:4.13.2")
    //testImplementation "org.junit.jupiter:junit-jupiter:5.10.2"
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("app.cash.turbine:turbine:1.1.0")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    // Explicit androidx.test:core so instrumented Compose tests can resolve
    // ApplicationProvider.getApplicationContext() (e.g. to build a real
    // SettingsRepositoryImpl-backed SettingsViewModel without a Hilt graph).
    // Previously it was only available transitively, which broke IDE/compile
    // resolution of androidx.test.core.app.ApplicationProvider.
    androidTestImplementation("androidx.test:core:1.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    // The Compose BOM must also be applied to the androidTest configuration.
    // Without it, version-less artifacts on this classpath (e.g. ui-test-junit4)
    // cannot be resolved and the full `./gradlew build` fails on
    // debugAndroidTestRuntimeClasspath. Pre-existing defect fixed as a chore
    // (see review round-2 report, item "build").
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.navigation:navigation-testing:2.8.0")

}
