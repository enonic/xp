// NoDB bench — latency baseline harness (p50/p95 get/save/children vs in-JVM stub),
// against client-java over a running server. Skeleton only in slice 1 step 1.

dependencies {
    implementation(project(":client-java"))
    implementation(libs.slf4j.api)

    testImplementation(libs.slf4j.simple)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}
