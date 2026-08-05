// NoDB engine — plain Java library: tenant provisioning, migrations, transactional stores.
// No OSGi, no Spring, no Flyway: explicit wiring throughout (DESIGN.md §8).

dependencies {
    implementation(libs.postgresql)
    implementation(libs.hikaricp)
    implementation(libs.slf4j.api)

    // Phase 4 Gate A. OpenSearch is spoken to over its JSON REST API with the JDK's own
    // java.net.http client -- no opensearch-java/rest-high-level client. Rationale: the
    // Phase-4 wire IS the JSON query DSL (BUILD-PHASE-4.md decision 1), so the translator's
    // job is JSON -> JSON; a typed client would force parse -> builder -> re-serialize round
    // trips and drag in httpclient5 for nothing. Jackson is the codec (api, not implementation:
    // the server module's NodeSearchService hands ObjectNode documents to the engine).
    // DESIGN.md §8's "no frameworks" rule is about OSGi/Spring/Flyway-style magic, not a
    // data codec -- hand-rolling a JSON parser for the riskiest wire in the project would be
    // the opposite of explicit. (Redeclared by the server module too -- `implementation`
    // deps don't leak transitively; same convention as its hikaricp/postgresql redeclaration.)
    implementation(libs.jackson.databind)
    // D8: ICU collation keys are computed here, not by the engine's analysis-icu plugin.
    implementation(libs.icu4j)

    // BinaryStore (Phase 2 Gate A): AWS SDK v2 S3 + STS clients over any S3-compatible
    // object store (MinIO in dev/test). Main-scope, not test-only -- the engine's
    // BinaryStore is a runtime dependency of the server, not just exercised by tests.
    implementation(platform(libs.awssdk.bom))
    implementation(libs.awssdk.s3)
    implementation(libs.awssdk.sts)

    testImplementation(libs.slf4j.simple)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.bundles.testcontainers)
    testImplementation(libs.testcontainers.minio)
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
