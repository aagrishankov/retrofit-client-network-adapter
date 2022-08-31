buildscript {
    repositories {
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
}

plugins {
    kotlin("jvm") version "1.6.21"
    id("maven-publish")
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

allprojects {
    group = "ru.grishankov.network.retrofit"
    version = "1.0.1"

    repositories {
        mavenLocal()
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(kotlin.sourceSets.getByName("main").kotlin.srcDirs)
}

publishing {
    publications {
        create<MavenPublication>("retrofit-network-adapter") {
            artifactId = "retrofit-network-adapter"
            group = "ru.grishankov.network.retrofit"
            version = "1.0.1"
            pom.packaging = "jar"
            artifact(sourcesJar.get())
        }
    }
}