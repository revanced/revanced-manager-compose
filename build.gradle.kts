buildscript {
    repositories {
        google()
    }
}

plugins {
    id("com.android.application") version "7.4.0-beta02" apply false
    id("com.android.library") version "7.4.0-beta02" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply false
    id("com.google.devtools.ksp") version "1.7.20-+" apply false
}
repositories {
    google()
}
