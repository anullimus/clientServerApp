plugins {
    java
    id("com.github.johnrengelman.shadow") version ("7.1.2")
}

group = "com.company.Labwork7Gradle"
version = "2.1"
val mainClass = "serverLogic.Server"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation(project(":general"))
    implementation("org.postgresql:postgresql:42.1.4")
    implementation("org.apache.logging.log4j:log4j-api:2.17.2")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.javadoc {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes(
            "Manifest-Version" to "1.0",
            "Main-Class" to mainClass
        )
    }
}
