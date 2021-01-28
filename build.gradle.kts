plugins {
    kotlin("jvm") version "1.4.21"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("org.springframework.boot") version "2.3.4.RELEASE"
    kotlin("plugin.spring") version "1.4.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.swinglabs:swingx:1.6.1")

    //implementation("org.springframework.boot:spring-boot-starter")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}