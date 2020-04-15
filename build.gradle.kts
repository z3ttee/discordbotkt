plugins {
    kotlin("jvm") version "1.3.71"
}

group = "de.zitzmanncedric"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"));
    implementation("com.discord4j:discord4j-core:3.0.14")
    implementation("com.discord4j:discord4j-voice:3.0.14")
    implementation("com.sedmelluq:lavaplayer:1.3.47")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.yaml:snakeyaml:1.26")
    implementation("org.reflections:reflections:0.9.12")
    implementation("commons-validator:commons-validator:1.6")

    // Google API
    implementation("com.google.api-client:google-api-client:1.23.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.23.0")
    implementation("com.google.apis:google-api-services-youtube:v3-rev221-1.25.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}