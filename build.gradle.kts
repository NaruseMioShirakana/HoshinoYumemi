plugins {
    val kotlinVersion = "1.5.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.9.2"
}

group = "srh.shirakana.hoshinoyumemi"
version = "0.0.1"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}
dependencies {
    implementation("com.tencentcloudapi:tencentcloud-sdk-java:4.0.11")
}