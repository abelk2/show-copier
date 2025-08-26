import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

plugins {
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.serialization") version "2.2.10"
    id("com.google.cloud.tools.jib") version "3.4.5"
}

group = "eu.abelk.showcopier"
version = "1.1.3"

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
    implementation("dev.inmo:krontab:2.7.2")
}

jib {
    from {
        image = "eclipse-temurin:21.0.8_9-jre-jammy"
        platforms {
            platform {
                architecture = "amd64"
                os = "linux"
            }
        }
    }
    to {
        image = "abelk/show-copier:${project.version}"
    }
    container {
        mainClass = "eu.abelk.showcopier.MainKt"
        creationTime =
            ZonedDateTime
                .now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_DATE_TIME)
    }
    setAllowInsecureRegistries(true)
}
