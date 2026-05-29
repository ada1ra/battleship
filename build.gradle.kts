plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.serialization") version "1.9.24"
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

javafx {
    version = "17.0.10"
    modules("javafx.controls", "javafx.fxml")
}

ktlint {
    version.set("1.3.0")
}
group = "edu.battleship"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // JUnit 5
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // TestFX + Monocle
    testImplementation("org.testfx:testfx-junit5:4.0.18")

    testImplementation("de.flapdoodle.fx21:openjfx-monocle-java17:1.0.1")

    // Mockito + расширение для JUnit 5
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.10.0")

    // AssertJ
    testImplementation("org.assertj:assertj-core:3.25.3")
}

tasks.test {
    useJUnitPlatform()

    // Настройки для headless-режима
    jvmArgs("-Djdk.attach.allowAttachSelf=true")
    systemProperties(
        "testfx.robot" to "glass",
        "glass.platform" to "Monocle",
        "monocle.platform" to "Headless",
        "prism.order" to "sw",
    )
}
application {
    mainClass.set("edu.battleship.MainKt")
}

kotlin {
    jvmToolchain(17)
}
