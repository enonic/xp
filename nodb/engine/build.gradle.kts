// NoDB engine — plain Java library: tenant provisioning, migrations, transactional stores.
// No OSGi, no Spring, no Flyway: explicit wiring throughout (DESIGN.md §8).

dependencies {
    implementation(libs.postgresql)
    implementation(libs.hikaricp)
    implementation(libs.slf4j.api)

    testImplementation(libs.slf4j.simple)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.bundles.testcontainers)
    testRuntimeOnly(libs.junit.platform.launcher)
}

// Testcontainers Docker socket detection. The Gradle daemon's environment is frozen at
// startup, so DOCKER_HOST on the gradlew invocation never reaches the daemon that forks
// tests. Set it on the forked test JVM instead. Docker Desktop on macOS puts the socket
// under the user home; fall back to the default when running against a plain daemon/CI.
tasks.test {
    val userHome = System.getProperty("user.home")
    val desktopSock = file("$userHome/.docker/run/docker.sock")
    if (System.getenv("DOCKER_HOST") == null && desktopSock.exists()) {
        environment("DOCKER_HOST", "unix://${desktopSock.absolutePath}")
        // Ryuk mounts the socket by path inside its container; override to the canonical path.
        environment("TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE", "/var/run/docker.sock")
    }
    // docker-java 3.4.0 (via testcontainers 1.20.4) defaults to Docker API 1.24, which
    // modern engines (>=29, min API 1.40) reject with HTTP 400. Pin a supported version.
    // Remove if testcontainers is bumped to a docker-java that negotiates the API version.
    systemProperty("api.version", "1.44")
}
