import org.kt3k.gradle.plugin.CoverallsPluginExtension

group = "VPR_MCMC"
version = "0.1"

plugins {
    kotlin("jvm") version "1.4.10"
    application
    jacoco
    id("com.github.kt3k.coveralls") version "2.8.2"
}

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://dl.bintray.com/kotlin/kotlin-dev")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.2.0-dev-7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
    implementation("io.github.microutils:kotlin-logging:2.0.3")
}

application {
    mainClassName = "ru.ifmo.ctd.novik.phylogeny.MainKt"
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    jacocoTestReport {
        reports {
            xml.isEnabled = true // coveralls plugin depends on xml format report
            html.isEnabled = true
        }
    }

    coveralls {
        saveAsFile = true
        sendToCoveralls = false
        sourceDirs.add("src/main/kotlin")
    }
}
