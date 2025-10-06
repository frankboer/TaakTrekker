plugins {
    id("java")
    id("conventions.java")
}

dependencies {
    implementation(libs.hikari)

    runtimeOnly(libs.postgres.driver)

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
