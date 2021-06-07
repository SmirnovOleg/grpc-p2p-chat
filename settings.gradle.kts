pluginManagement {
    val `kotlin-version`: String by settings
    val `protobuf-plugin`: String by settings
    val `javafxplugin-version`: String by settings


    plugins {
        kotlin("jvm") version `kotlin-version`
        id("com.google.protobuf") version `protobuf-plugin`
        id("org.openjfx.javafxplugin") version `javafxplugin-version`
    }
}

rootProject.name = "grpc-chat"

