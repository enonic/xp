// NoDB client-java — thin gRPC client lib (later becomes XP's nodb-client core).
//
// java-library (not just the root build's plain `java` plugin) so `api(...)` below is
// available: :bench needs server's generated proto/grpc classes too (to build
// WriteBatchRequest/Version/BranchEntry messages directly), and should get them by
// depending on :client-java alone rather than redeclaring :server itself.
plugins {
    id("java-library")
}
//
// Generated stub source: this module deliberately does NOT re-run the protobuf/grpc
// codegen against nodb.proto (that would be a second codegen output from the same
// .proto file, easy to let drift from server's). Simplest path for slice 1: depend on
// :server directly and reuse ITS generated classes (NodeStoreGrpc, WriteBatchRequest,
// ...). This does pull server's own runtime deps (engine, HikariCP, postgresql, java-jwt)
// onto client-java's runtime classpath too, which is not architecturally clean — a real
// follow-up would split proto codegen into its own module/artifact that both server and
// client-java depend on. Not worth building for a slice-1 bench harness that always runs
// client-java alongside a full server anyway; noted here as a deliberate shortcut.
dependencies {
    api(project(":server"))

    implementation(libs.bundles.grpc.server)
    implementation(libs.protobuf.java)
    implementation(libs.slf4j.api)

    testImplementation(libs.slf4j.simple)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}
