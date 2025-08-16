plugins {
    id("conventions.java")
}

dependencies {
    implementation(libs.hikari)
    runtimeOnly(libs.postgres.driver)

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
