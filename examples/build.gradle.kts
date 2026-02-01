plugins {
    id("conventions.java")
    id("conventions.openrewrite")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}


dependencies {
    implementation(project(":taaktrekker-core"))
    implementation(project(":taaktrekker-spring"))
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}
