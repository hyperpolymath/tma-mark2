// SPDX-License-Identifier: MPL-2.0
// eTMA Handler - Modernised Java Application

plugins {
    java
    application
}

group = "uk.ac.open"
version = "2.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Jakarta Mail (replacing javax.mail)
    implementation("org.eclipse.angus:angus-mail:2.0.4")
    implementation("jakarta.mail:jakarta.mail-api:2.1.5")
    implementation("jakarta.activation:jakarta.activation-api:2.1.3")

    // Apache Commons utilities
    implementation("org.apache.commons:commons-lang3:3.20.0")
    implementation("commons-codec:commons-codec:1.19.0")

    // Spell checking (Jazzy successor)
    implementation("org.languagetool:language-en:6.4")

    // TOML configuration
    implementation("com.moandjiezana.toml:toml4j:0.7.2")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.15")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation("org.mockito:mockito-core:5.15.2")
}

application {
    mainClass = "uk.ac.open.etma.EtmaApplication"
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "uk.ac.open.etma.EtmaApplication",
            "Implementation-Title" to "eTMA Handler",
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "Open University Tutors"
        )
    }
}
