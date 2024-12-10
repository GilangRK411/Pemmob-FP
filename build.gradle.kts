plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

buildscript {
    repositories {
        google()  // Pastikan repositori google ada
        mavenCentral()
    }
    dependencies {
        // Menambahkan classpath untuk plugin google-services
        classpath("com.google.gms:google-services:4.3.15")  // Versi terbaru
    }
}
