// NoDB bench — latency baseline harness (p50/p95/p99 get/getByPath/children/WriteBatch),
// against client-java over a REAL (loopback TCP) NodbServer backed by testcontainers
// postgres (slice 1 step 6, retires DESIGN.md §10 risk #2). The bench harness embeds its
// own server + Postgres container (see BenchEnvironment) so it also needs :server (to
// start NodbServer, mint dev tokens) and :engine (TenantContext/TenantProvisioner) directly
// — client-java's `api(project(":server"))` exposes server's generated proto/grpc classes
// transitively, but NOT engine's (server depends on engine as `implementation`), so engine
// is declared here explicitly.
//
// testcontainers-postgresql is a MAIN dependency (not testImplementation): BenchHarness's
// runnable `main` starts a container directly (no JUnit), same library, just used outside
// a @Testcontainers-annotated test class.
//
// `application` plugin so `../gradlew :bench:run` runs BenchHarness.main directly (the
// full ~100k-node bench) without hand-assembling a classpath.
plugins {
    application
}

application {
    mainClass.set("com.enonic.nodb.bench.BenchHarness")
}

dependencies {
    implementation(project(":client-java"))
    implementation(project(":engine"))
    implementation(libs.protobuf.java)
    implementation(libs.hikaricp)
    implementation(libs.postgresql)
    implementation(libs.testcontainers.postgresql)
    implementation(libs.slf4j.api)

    testImplementation(libs.slf4j.simple)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.bundles.testcontainers)
    // Phase-3 Gate-0 scratch (Gate0PayloadPathBenchTest) builds its own raw NodeStoreGrpc
    // stub (CallCredentials/ManagedChannel/Metadata) to reach StoreVersion/StoreBranchEntry,
    // which NodbClient's thin surface does not expose -- client-java depends on this bundle
    // only as `implementation`, so it does not reach bench's classpath transitively.
    testImplementation(libs.bundles.grpc.server)
    testRuntimeOnly(libs.junit.platform.launcher)
}

// Same Docker-socket/API-version wiring as engine/server — see engine/build.gradle.kts
// for the full rationale. BenchEnvironment always starts its Postgres container
// programmatically (no @Testcontainers JUnit extension anywhere in this module), but the
// forked test JVM still needs the same DOCKER_HOST/api.version wiring as engine/server's
// tests do, for the same reason.
tasks.test {
    val userHome = System.getProperty("user.home")
    val desktopSock = file("$userHome/.docker/run/docker.sock")
    if (System.getenv("DOCKER_HOST") == null && desktopSock.exists()) {
        environment("DOCKER_HOST", "unix://${desktopSock.absolutePath}")
        environment("TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE", "/var/run/docker.sock")
    }
    systemProperty("api.version", "1.44")
    // Full 100k run: ../gradlew :bench:test -Dnodb.bench.full=true --tests BenchHarnessTest
    System.getProperty("nodb.bench.full")?.let { systemProperty("nodb.bench.full", it) }
    // The 100k-node full run buffers enough to OOM the default-heap test worker; an OOM
    // kills the worker mid-run and surfaces as a spurious NoSuchFileException on Gradle's
    // in-progress test-results bin. The reduced (build-default) run fits easily; this
    // headroom just keeps the opt-in full run green too. Canonical full bench is :bench:run.
    maxHeapSize = "2g"
}

// BenchHarness.main starts its own Testcontainers Postgres directly (no @Testcontainers
// JUnit extension involved for the `run` task), so it needs the same Docker-socket
// detection as tasks.test above — the forked JavaExec JVM doesn't otherwise see a
// DOCKER_HOST set on the `../gradlew` invocation (the Gradle daemon's env is frozen at
// daemon startup; see engine/build.gradle.kts).
tasks.named<JavaExec>("run") {
    val userHome = System.getProperty("user.home")
    val desktopSock = file("$userHome/.docker/run/docker.sock")
    if (System.getenv("DOCKER_HOST") == null && desktopSock.exists()) {
        environment("DOCKER_HOST", "unix://${desktopSock.absolutePath}")
        environment("TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE", "/var/run/docker.sock")
    }
    systemProperty("api.version", "1.44")
    maxHeapSize = "2g"
    workingDir = projectDir
}
