// NoDB client-java — thin gRPC client lib (later becomes XP's nodb-client core).
// Skeleton only in slice 1 step 1; real content lands with the gRPC server (step 5).

dependencies {
    implementation(libs.slf4j.api)

    testImplementation(libs.slf4j.simple)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}
