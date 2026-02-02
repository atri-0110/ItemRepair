plugins {
    id("java-library")
    id("org.allaymc.gradle.plugin") version "0.2.1"
}

group = "org.allaymc.itemrepair"
description = "A plugin that allows players to repair damaged items and tools using experience levels"
version = "0.1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// See also https://github.com/AllayMC/AllayGradle
allay {
    api = "0.24.0"

    plugin {
        entrance = ".ItemRepairPlugin"
        authors += "atri-0110"
        website = "https://github.com/atri-0110/ItemRepair"
    }
}

dependencies {
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.34")
    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.34")
    implementation("com.google.code.gson:gson:2.10.1")
}

repositories {
    mavenCentral()
}
