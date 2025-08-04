plugins {
    `java-library`
    `maven-publish`
    id("com.diffplug.spotless")
    id ("jacoco")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

spotless {
    // optional: limit format enforcement to just the files changed by this feature branch
    ratchetFrom("origin/main")

    isEnforceCheck = false

    java {
        palantirJavaFormat()
        removeUnusedImports()
        endWithNewline()
        trimTrailingWhitespace()


        cleanthat()
            .sourceCompatibility("21")
            .addMutator("SafeAndConsensual")
            .addMutator("SafeButNotConsensual")
            .addMutator("SafeButControversial")
            .includeDraft(true)
    }
}