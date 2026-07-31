// plugins block - plugin versions are pinned at the root and shared by all modules;
// apply false defers plugin application to the modules that actually use them
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false

    id("com.google.dagger.hilt.android") version "2.57.2" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.25" apply false

    id("androidx.navigation.safeargs.kotlin") version "2.9.7" apply false
}
