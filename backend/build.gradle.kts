plugins {
    // Kotlin-Version muss zu detekt passen (detekt 2.0.0-alpha.6 wurde mit 2.4.10 gebaut).
    kotlin("jvm") version "2.4.10"
    kotlin("plugin.spring") version "2.4.10"
    id("org.springframework.boot") version "3.5.16"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    id("dev.detekt") version "2.0.0-alpha.6"
}

group = "de.familiendashboard"
version = "0.0.1-SNAPSHOT"

// Spring Boot 3.5 verwaltet standardmäßig Kotlin 1.9.25. Wir nutzen die neuere Version.
extra["kotlin.version"] = "2.4.10"

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

detekt {
    buildUponDefaultConfig = true
}

// Der einfache Task "detekt" arbeitet ohne Typauflösung und übersieht z. B. "!!" (R12).
// Deshalb führt "detekt" immer auch die Varianten mit Typauflösung aus (Hook und CI).
tasks.named("detekt") {
    dependsOn("detektMain", "detektTest")
}
