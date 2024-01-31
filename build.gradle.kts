import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    val versions = object {
        val kotlin = "1.9.22"
        val shadow = "8.1.1"
        val versions = "0.51.0"
    }

    kotlin("jvm") version(versions.kotlin)
    kotlin("plugin.allopen") version(versions.kotlin)

    id("com.github.johnrengelman.shadow") version(versions.shadow)
    id("com.github.ben-manes.versions") version(versions.versions) // ./gradlew dependencyUpdates to check for new versions
    application
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    val versions = object {
        val caffeine = "3.1.8"
        val dittnavCommon = "2022.09.30-12.41-aa46d2d75788"
        val tmsKtorTokenSupport = "4.0.0"
        val jackson = "2.16.1"
        val junit = "5.10.1"
        val kluent = "1.73"
        val kotlinxCoroutines = "1.7.3"
        val kotlinxHtmlJvm = "0.11.0"
        val ktor = "2.3.7"
        val logback = "1.4.14"
        val logstash = "7.4"
        val micrometer = "1.12.2"
        val mockk = "1.13.9"
        val navSecurity = "4.1.0"
    }

    implementation("com.github.navikt.dittnav-common-lib:dittnav-common-utils:${versions.dittnavCommon}")
    implementation("com.github.navikt.tms-ktor-token-support:azure-exchange:${versions.tmsKtorTokenSupport}")
    implementation("com.github.ben-manes.caffeine:caffeine:${versions.caffeine}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${versions.jackson}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${versions.jackson}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.kotlinxCoroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${versions.kotlinxHtmlJvm}")
    implementation("io.ktor:ktor-client-apache:${versions.ktor}")
    implementation("io.ktor:ktor-client-jackson:${versions.ktor}")
    implementation("io.ktor:ktor-client-json:${versions.ktor}")
    implementation("io.ktor:ktor-client-logging:${versions.ktor}")
    implementation("io.ktor:ktor-client-logging-jvm:${versions.ktor}")
    implementation("io.ktor:ktor-client-serialization-jvm:${versions.ktor}")
    implementation("io.ktor:ktor-client-content-negotiation:${versions.ktor}")
    implementation("io.ktor:ktor-server-content-negotiation:${versions.ktor}")
    implementation("io.ktor:ktor-server-html-builder:${versions.ktor}")
    implementation("io.ktor:ktor-serialization-jackson:${versions.ktor}")
    implementation("io.ktor:ktor-server-netty:${versions.ktor}")
    implementation("io.ktor:ktor-server-default-headers:${versions.ktor}")
    implementation("io.ktor:ktor-server-cors:${versions.ktor}")
    implementation("io.ktor:ktor-server-metrics-micrometer:${versions.ktor}")
    implementation("io.micrometer:micrometer-registry-prometheus:${versions.micrometer}")
    implementation("ch.qos.logback:logback-classic:${versions.logback}")
    implementation("net.logstash.logback:logstash-logback-encoder:${versions.logstash}")
    implementation("no.nav.security:token-validation-ktor-v2:${versions.navSecurity}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${versions.junit}")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${versions.junit}")
    testImplementation("org.amshove.kluent:kluent:${versions.kluent}")
    testImplementation("io.ktor:ktor-client-mock:${versions.ktor}")
    testImplementation("io.ktor:ktor-client-mock-jvm:${versions.ktor}")
    testImplementation("io.ktor:ktor-server-test-host-jvm:${versions.ktor}")
    testImplementation("io.mockk:mockk:${versions.mockk}")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            events("passed", "skipped", "failed")
        }
    }
}
