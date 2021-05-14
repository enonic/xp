# Enonic XP Javascript Libraries

This document describes the various standard libraries shipped with Enonic XP. The libraries are included in your
application through [Gradle](http://gradle.org/) build script. To add a library to your build file, add it using the ``include``
scope like this:

    include 'com.enonic.xp:<name>:<version>'

Where ``name`` is the library name and ``version`` is the current Enonic XP version. Here's an example adding ``lib-auth`` dependency:

    dependencies {
      include 'com.enonic.xp:lib-auth:<version>'
    }
