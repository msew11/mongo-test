plugins {
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.noarg") version "1.9.21"
}

group = "org.matrix.game"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mongodb:mongodb-driver-sync:4.7.1")
    implementation("org.springframework.data:spring-data-mongodb:3.4.2")
    implementation("org.reflections:reflections:0.9.11")

    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation(kotlin("reflect"))

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

noArg {
    annotation("org.matrix.game.NoArg")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}