// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("io.gitlab.arturbosch.detekt").version("1.23.7")
}

buildscript {
    repositories {
        mavenCentral()
    }
}
