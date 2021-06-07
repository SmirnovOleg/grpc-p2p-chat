import com.google.protobuf.gradle.*
import org.gradle.kotlin.dsl.proto
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    application
    kotlin("jvm")
    id("com.google.protobuf")
    id("org.openjfx.javafxplugin")
}

application {
    mainClass.set("org.example.chat.MainAppKt")
}

group = "org.example.chat"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val `kotlin-coroutines`: String by project
val `protobuf-version`: String by project
val `grpc-java`: String by project
val `grpc-kotlin`: String by project
val `grpc-annotations`: String by project
val `tornadofx-version`: String by project
val `javafx-version`: String by project


dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$`kotlin-coroutines`")

    implementation("io.grpc:grpc-kotlin-stub:$`grpc-kotlin`")
    implementation("com.google.protobuf:protobuf-java-util:$`protobuf-version`")
    implementation("org.apache.tomcat:annotations-api:$`grpc-annotations`")
    implementation("io.grpc:grpc-netty-shaded:$`grpc-java`")

    implementation("no.tornado:tornadofx:$`tornadofx-version`") {
        exclude("org.jetbrains.kotlin")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$`protobuf-version`"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$`grpc-java`"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$`grpc-kotlin`:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}

javafx {
    version = `javafx-version`
    modules("javafx.controls")
}

sourceSets {
    main {
        proto {
            srcDir("src/main/proto")
        }
        java {
            srcDirs(
                "build/generated/source/proto/main/grpc",
                "build/generated/source/proto/main/java"
            )
        }
        withConvention(KotlinSourceSet::class) {
            kotlin.srcDirs("build/generated/source/proto/main/grpckt")
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}