<img align="right" src="https://raw.githubusercontent.com/enonic/xp/master/misc/logo.png">

# Enonic XP

[![Build Status](https://travis-ci.org/enonic/xp.svg?branch=master)](https://travis-ci.org/enonic/xp)
[![Codecov](https://codecov.io/gh/enonic/xp/branch/master/graph/badge.svg)](https://codecov.io/gh/enonic/xp)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ceca6f602c2a43e7a2f32287e202fe2c)](https://www.codacy.com/app/enonic/xp?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=enonic/xp&amp;utm_campaign=Badge_Grade)

Welcome to the home of Enonic XP. Here you will find all source code for the product. To get started,
please read our docs here: http://xp.readthedocs.org.

## Building

Before trying to build the project, you need to verify that the following software are installed:

* Java 11 for building and running.
* Gradle 6.x build system.
* Git installed on system.

Build all code and run all tests including integration tests:

    gradle build

Build all code skipping all tests:

    gradle build -x check

Build all code skipping integration tests:

    gradle build -x integrationTest

## Running

This project is just the runtime of the Enonic XP platform.  In order to run the system properly,
please see the xp-distro project that bundles the necessary part together: (https://github.com/enonic/xp-distro)

## License

This software is licensed under GPL v3. See [LICENSE.txt](https://github.com/enonic/xp/raw/master/LICENSE.txt). 
Also the distribution includes 3rd party software components. The vast majority of these libraries are licensed under 
Apache 2.0. For a complete list please read [NOTICE.txt](https://github.com/enonic/xp/raw/master/NOTICE.txt).

All our libraries (`lib-*`) that can be bundled in your own applications are licensed as Apache 2.0. 
See [LICENSE_AL.txt](https://github.com/enonic/xp/raw/master/LICENSE_AL.txt)

