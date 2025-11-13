<img align="right" src="https://raw.githubusercontent.com/enonic/xp/master/misc/logo.png">

# Enonic XP

[![Actions Status](https://github.com/enonic/xp/workflows/Java%20CI/badge.svg)](https://github.com/enonic/xp/actions)
[![Codecov](https://codecov.io/gh/enonic/xp/branch/master/graph/badge.svg)](https://codecov.io/gh/enonic/xp)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/1c21f9de69f0444797abdeea49a682e6)](https://www.codacy.com/gh/enonic/xp/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=enonic/xp&amp;utm_campaign=Badge_Grade)

Welcome to the home of Enonic XP. Here you will find all source code for the product. To get started,
please read our docs here: https://developer.enonic.com/start.

## Building

Before trying to build the project, you need to verify that the following software are installed:

*    [JDK 11](https://adoptium.net/temurin/archive/?version=11) for building and [GraalVM Java 21](https://www.graalvm.org/downloads/) for running.   
*    [Git](https://git-scm.com/downloads) installed on system.

Build all code and run all tests including integration tests:

    .\gradlew build

Build all code skipping all tests:

    .\gradlew build -x check

Build all code skipping integration tests:

    .\gradlew build -x integrationTest

Main output of the build process is located in the `moduels/runtime/build` directory
*   `install` contains pure runtime of the Enonic XP platform.
*   `distributions` contains a zip file ready for packaging by the xp-distro project.

Jsdoc output is located in the `modules/lib/build/distributions` directory.

## Running

This project is just the runtime of the Enonic XP platform.  In order to run the system properly,
please see the xp-distro project that bundles the necessary part together: (https://github.com/enonic/xp-distro)

## Documentation

*   [Developer Guide](https://developer.enonic.com/docs/xp/stable)
*   [Release Notes](https://developer.enonic.com/docs/xp/stable/release)
*   [JSDoc](https://developer.enonic.com/jsdoc/) 

## License

This software is licensed under GPL v3. See [LICENSE.txt](https://github.com/enonic/xp/raw/master/LICENSE.txt). 
Also, the distribution includes 3rd party software components. The vast majority of these libraries are licensed under 
Apache 2.0. For a complete list please read [NOTICE.txt](https://github.com/enonic/xp/raw/master/NOTICE.txt).

All our libraries (`lib-*`) that can be bundled in your own applications are licensed as Apache 2.0. 
See [LICENSE_AL.txt](https://github.com/enonic/xp/raw/master/LICENSE_AL.txt)

