// NoDB server — gRPC bindings over the engine. Skeleton only in slice 1 step 1;
// real content (proto codegen, auth interceptor, dev issuer) lands in later steps.

dependencies {
    implementation(project(":engine"))
    implementation(libs.slf4j.api)

    testImplementation(libs.slf4j.simple)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}
