// NoDB — standalone Gradle build. Deliberately independent of the XP root build
// (/Users/tls/work/xp/settings.gradle): NoDB has no OSGi, no Spring, plain Java only.
// Run from this directory: `../gradlew build` (uses XP's Gradle wrapper, but this
// settings file defines an entirely separate project graph).

rootProject.name = "nodb"

include("engine", "server", "client-java", "bench")
