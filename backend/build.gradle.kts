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
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Zwei Teststufen: "test" ist schnell (Stop-Hook), "integrationTest" startet echte Container (CI).
tasks.test {
    useJUnitPlatform {
        excludeTags("integration")
    }
}

val testSourceSet = sourceSets.test.get()

val integrationTest by tasks.registering(Test::class) {
    description = "Tests mit echter Datenbank (Testcontainers), brauchen Docker."
    group = "verification"
    testClassesDirs = testSourceSet.output.classesDirs
    classpath = testSourceSet.runtimeClasspath
    useJUnitPlatform {
        includeTags("integration")
    }
    shouldRunAfter(tasks.test)
}

tasks.check {
    dependsOn(integrationTest)
}

// Fester Dateiname, damit das Dockerfile das JAR ohne Versionsnummer findet.
tasks.bootJar {
    archiveFileName.set("app.jar")
}

detekt {
    buildUponDefaultConfig = true
}

// Der einfache Task "detekt" arbeitet ohne Typauflösung und übersieht z. B. "!!" (R12).
// Deshalb führt "detekt" immer auch die Varianten mit Typauflösung aus (Hook und CI).
tasks.named("detekt") {
    dependsOn("detektMain", "detektTest")
}
