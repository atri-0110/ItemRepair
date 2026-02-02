plugins {
    id("java")
    id("allay") version "0.2.1"
}

allay {
    api = "0.24.0"
    plugin {
        name = "ItemRepair"
        version = "0.1.0"
        main = "org.allaymc.itemrepair.ItemRepairPlugin"
        description = "A plugin that allows players to repair damaged items and tools using experience levels or special repair items."
        authors += "atri-0110"
        website = "https://github.com/atri-0110/ItemRepair"
        apiVersion = "0.24.0"
        loadBefore = emptyList()
        loadAfter = emptyList()
        softDepends = emptyList()
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "21"
    targetCompatibility = "21"
}
