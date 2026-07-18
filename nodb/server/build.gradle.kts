// NoDB server — gRPC bindings over the engine (slice 1 step 5): proto codegen, JWT auth
// interceptor, dev token issuer, service impls wired to the engine's stores/WriteService.

plugins {
    id("com.google.protobuf") version "0.10.0"
    application
}

// `application` plugin so `../gradlew :server:installDist` produces a plain launcher
// script (build/install/server/bin/server) with a fully-assembled lib/*.jar classpath --
// same rationale as nodb/bench's own `application` usage. Added in Phase 1 Gate D
// (nodb/BUILD-PHASE-1.md) to boot a real standalone NodbServer for the XP smoke test
// without hand-assembling a classpath or fighting the Gradle daemon's frozen-env
// behavior (see nodb/bench/build.gradle.kts's comment on that) -- the installed script
// is a plain shell process, env vars just work. The same lib/ directory doubles as the
// classpath for the other small mains in this module (NodbTokenTool, TenantBootstrapTool):
// `java -cp build/install/server/lib/* <FQCN> ...`.
application {
    mainClass.set("com.enonic.nodb.server.NodbServer")
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
