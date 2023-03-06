plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"
val lombokVersion = "1.18.24"
val mapstructVersion = "1.5.3.Final"
val mockitoVersion = "4.10.0"
val junitVersion = "5.8.1"
val slf4jVersion = "2.0.6"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
