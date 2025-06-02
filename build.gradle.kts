plugins {
    id("java")
    id("io.freefair.lombok") version "8.12.2.1"
    id("io.github.goooler.shadow") version "8.1.8"
}

group = "com.xironite"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "aikar"
        url = uri("https://repo.aikar.co/content/groups/aikar/")
    }
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "packet events"
        url = uri("https://repo.codemc.io/repository/maven-releases/")
    }
    maven {
        name = "world guard"
        url = uri("https://maven.enginehub.org/repo/")
    }
}

dependencies {
    // Explicitly declare JUnit dependencies for tests
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.8.0")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.13")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")

    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    testCompileOnly("org.projectlombok:lombok:1.18.36")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.36")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

tasks.test {
    useJUnitPlatform()

    // Option 1: If you don't have any tests, just disable the test task
    // enabled = false

    // Option 2: Skip the test task if no test source files exist
    onlyIf {
        project.hasProperty("runTests") || fileTree("src/test").files.any { it.name.endsWith(".java") }
    }

    // Fix for warning about tests not being executed
    filter {
        isFailOnNoMatchingTests = false
    }
}

tasks.shadowJar {
    relocate("co.aikar.commands", "com.xironite.acf")
    relocate("co.aikar.locales", "com.xironite.locales")

    archiveClassifier.set("")
}

tasks.named("build") {
    dependsOn(tasks.shadowJar)
}