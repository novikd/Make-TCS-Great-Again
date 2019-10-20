group = "Make-TCS-Great-Again"
version = "0.1"

plugins {
    kotlin("jvm") version "1.3.50"
    application
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib", "1.3.50"))
}

application {
    mainClassName = "ru.ifmo.ctd.novik.phylogeny.MainKt"
}
