plugins {
    val kotlinVersion = "1.5.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.9.2"
}

group = "srh.shirakana.hoshinoyumemi"
version = "0.0.3"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}
dependencies {
    implementation("com.tencentcloudapi:tencentcloud-sdk-java:4.0.11")
    implementation("com.alibaba:fastjson:2.0.3")
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
}