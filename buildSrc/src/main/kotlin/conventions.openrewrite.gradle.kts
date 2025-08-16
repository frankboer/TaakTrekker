plugins {
    `java-library`
    `maven-publish`
    id("org.openrewrite.rewrite")
    id ("jacoco")
}

rewrite {
    activeRecipe("org.openrewrite.staticanalysis.CodeCleanup")
    setExportDatatables(true)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    rewrite("org.openrewrite.recipe:rewrite-static-analysis:2.15.0")
}