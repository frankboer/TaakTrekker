plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
}

dependencies {
    api("com.diffplug.spotless", "spotless-plugin-gradle", "7.0.2")
    implementation("io.freefair.lombok:io.freefair.lombok.gradle.plugin:8.14")
}