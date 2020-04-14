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
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}