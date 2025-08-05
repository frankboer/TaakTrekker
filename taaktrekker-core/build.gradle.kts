plugins {
    id("java")
    id("conventions.java")
}

dependencies {
    runtimeOnly(libs.postgres.driver)

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}