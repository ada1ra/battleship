plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.serialization") version "1.9.24"
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

javafx {
    version = "21"
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
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("edu.battleship.MainKt")
    applicationDefaultJvmArgs =
        listOf(
            "--add-opens=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED",
            "-Dprism.verbose=false",
            "-Djavafx.verbose=false",
        )
}

kotlin {
    jvmToolchain(17)
}
