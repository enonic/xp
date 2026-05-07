<div align="center">

<img src="https://raw.githubusercontent.com/enonic/xp/master/misc/logo.png">

# Enonic XP


[![Actions Status](https://github.com/enonic/xp/workflows/Gradle%20Build/badge.svg)](https://github.com/enonic/xp/actions)
[![Codecov](https://codecov.io/gh/enonic/xp/branch/master/graph/badge.svg)](https://codecov.io/gh/enonic/xp)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/1c21f9de69f0444797abdeea49a682e6)](https://www.codacy.com/gh/enonic/xp/dashboard?utm_source=github.com&utm_medium=referral&utm_content=enonic/xp&utm_campaign=Badge_Grade)

</div>

## The Universal CMS

Enonic XP is an open-source, API-first CMS designed as the backend for digital experiences. It exposes your content and services through flexible APIs that adapt to whatever front-end stack you choose — whether that's Next.js, Astro, a mobile app, or anything in between.   

Built like an operating system around a lean, extensible core, XP combines a server-side TypeScript/JavaScript framework with a built-in NoSQL content store, identity management, and a universal API layer — all fully open source, with no vendor lock-in.     

## Quick Start

To get started with Enonic, visit **[developer.enonic.com/start](https://developer.enonic.com/start)**.

## Features

- **Headless CMS** — Ships with Content Studio; works with any front-end framework
- **Universal API** — Apps contribute their own API endpoints through a Kubernetes-inspired extension model
- **Built-in NoSQL content store** — Versioned, branch-based content management with a flexible schema system
- **Next.js integration** — First-class support with dedicated React components
- **Identity & Access Management** — Authentication, authorization, and pluggable ID providers built in
- **Server-side TypeScript/JavaScript** — Write apps in TypeScript, deployed without Node.js or a separate JS process
- **Multi-app runtime** — Run multiple applications simultaneously on a single instance
- **Clustering** — Distributed caching, event bus, and job scheduling included
- **25+ standard libraries** — Content, auth, mail, scheduling, audit logging, and more

## Documentation

| Resource | URL |
|---|---|
| XP Platform Guide | https://developer.enonic.com/docs/xp |
| CMS Guide (front-end devs) | https://developer.enonic.com/docs/cms |
| Developer Tooling | https://developer.enonic.com/docs/code |
| Release Notes | https://developer.enonic.com/docs/xp/stable/release |

## Building

**Prerequisites:**
- [JDK 25](https://adoptium.net/temurin/releases?version=25) or [GraalVM 25](https://www.graalvm.org/downloads/) — GraalVM is required to *run* the platform
- [Git](https://git-scm.com/downloads)

```sh
# Build everything, including integration tests
./gradlew build

# Skip all tests
./gradlew build -x check

# Skip integration tests only
./gradlew build -x integrationTest
```

Build output is in `modules/runtime/build/`:
- `install/` — the bare platform runtime
- `distributions/` — zip for packaging by xp-distro

JSDoc output is in `modules/lib/build/distributions/`.

## License

Enonic XP is licensed under **GPL v3** with a [Linking Exception](https://en.wikipedia.org/wiki/GPL_linking_exception) — you can build and ship applications on the platform without licensing them under GPL. Derivative works of the platform itself remain subject to GPL.

- Platform: [LICENSE.txt](https://github.com/enonic/xp/raw/master/LICENSE.txt)
- Third-party components (mostly Apache 2.0): [NOTICE.txt](https://github.com/enonic/xp/raw/master/NOTICE.txt)

All `lib-*` libraries bundleable in your own applications are licensed under **Apache 2.0**: [LICENSE_AL.txt](https://github.com/enonic/xp/raw/master/LICENSE_AL.txt)

## Support

- [Developer Portal](https://developer.enonic.com)
- [Slack Community](https://slack.enonic.com)
- [Commercial Support](https://support.enonic.com)
