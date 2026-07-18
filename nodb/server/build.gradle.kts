// NoDB server — gRPC bindings over the engine (slice 1 step 5): proto codegen, JWT auth
// interceptor, dev token issuer, service impls wired to the engine's stores/WriteService.

plugins {
    id("com.google.protobuf") version "0.10.0"
}

dependencies {
    implementation(project(":engine"))
    implementation(libs.slf4j.api)
    implementation(libs.hikaricp)
    implementation(libs.postgresql)

    implementation(libs.bundles.grpc.server)
    implementation(libs.protobuf.java)
    compileOnly(libs.javax.annotation.api)
    testCompileOnly(libs.javax.annotation.api)

    implementation(libs.java.jwt)

    testImplementation(libs.slf4j.simple)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.bundles.testcontainers)
    testImplementation(libs.grpc.inprocess)
    testRuntimeOnly(libs.junit.platform.launcher)
}

val protobufVersion = libs.versions.protobuf.get()
val grpcVersion = libs.versions.grpc.get()

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
            }
        }
    }
}

// See engine/build.gradle.kts for why this block exists (testcontainers Docker socket
// detection under the Gradle daemon); server integration tests use the same containers.
tasks.test {
    val userHome = System.getProperty("user.home")
    val desktopSock = file("$userHome/.docker/run/docker.sock")
    if (System.getenv("DOCKER_HOST") == null && desktopSock.exists()) {
        environment("DOCKER_HOST", "unix://${desktopSock.absolutePath}")
        environment("TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE", "/var/run/docker.sock")
    }
    systemProperty("api.version", "1.44")
}
