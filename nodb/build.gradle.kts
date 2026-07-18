// Root build for the standalone NoDB project. Plain Java throughout — no OSGi, no Spring
// (see DESIGN.md §8). Shared conventions only; each module declares its own dependencies.

allprojects {
    group = "com.enonic.nodb"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        maxHeapSize = "1g"
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}
