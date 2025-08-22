plugins {
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.serialization") version "2.2.10"
}

group = "eu.abelk.showcopier"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("io.ktor:ktor-client-cio-jvm:3.2.3")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:3.2.3")
    implementation("io.ktor:ktor-client-logging:3.2.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.2.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.7.1")
    implementation("info.picocli:picocli:4.7.7")
    implementation("org.apache.commons:commons-lang3:3.18.0")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("com.github.fracpete:rsync4j-core:3.3.0-5")
    implementation("com.github.fracpete:processoutput4j:0.1.0")
    implementation("dev.inmo:krontab:2.7.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}