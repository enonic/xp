#!/usr/bin/env bash

DIRNAME=$(dirname "$0")
PROGNAME=$(basename "$0")
ARG1=$1
ARGS=("$@")

XP_SCRIPT=$PROGNAME
export XP_SCRIPT
if [ -f "$DIRNAME/setenv.sh" ]; then
  . "$DIRNAME/setenv.sh"
fi

die() {
    warn "$*"
    exit 1
}

warn() {
    echo "${PROGNAME}: $*"
}

locateJava() {
    if [ -n "$JAVA_HOME" ] ; then
        if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
            JAVACMD="$JAVA_HOME/jre/sh/java"
        else
            JAVACMD="$JAVA_HOME/bin/java"
        fi
        if [ ! -x "$JAVACMD" ] ; then
            die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME."
        fi
    else
        JAVACMD="java"
        which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
    fi
}

setupDefaults() {
    DEFAULT_JAVA_OPTS="-XX:-OmitStackTraceInFastThrow -XX:+AlwaysPreTouch"
    DEFAULT_JAVA_DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
    CONSTANT_XP_OPTS=(-Dfile.encoding=UTF8 -Dmapper.allow_dots_in_name=true --add-exports java.base/jdk.internal.ref=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.management/sun.management=ALL-UNNAMED --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED)
}

setupOptions() {
    if [ -z "$JAVA_OPTS" ]; then
        JAVA_OPTS="$DEFAULT_JAVA_OPTS"
    fi
    export JAVA_OPTS
}

setupDebugOptions() {
    if [ "$ARG1" = "debug" ]; then
        if [ -z "$JAVA_DEBUG_OPTS" ]; then
            JAVA_DEBUG_OPTS="$DEFAULT_JAVA_DEBUG_OPTS"
        fi

        JAVA_OPTS="$JAVA_DEBUG_OPTS $JAVA_OPTS"
    fi
}

locateInstallDir() {
    XP_INSTALL=$(cd "$DIRNAME/.." || exit; pwd)
    if [ ! -d "$XP_INSTALL" ]; then
        die "XP_INSTALL is not valid: $XP_INSTALL"
    fi
}

setupTmpDir() {
  tmpdir=${XP_TMP:-${XP_HOME:+$XP_HOME/work}}
  tmpdir=${tmpdir:-$XP_INSTALL/home/work}
  JAVA_OPTS="-Djava.io.tmpdir=$tmpdir $JAVA_OPTS"
}

init() {
    locateJava
    setupDefaults
    setupOptions
    setupDebugOptions
    locateInstallDir
    setupTmpDir
}

run() {
    exec "$JAVACMD" $JAVA_OPTS -Dxp.install="$XP_INSTALL" $XP_OPTS "${CONSTANT_XP_OPTS[@]}" --module-path "$XP_INSTALL/mods" -classpath "$XP_INSTALL/lib/*" com.enonic.xp.launcher.LauncherMain "${ARGS[@]}"
}

main() {
    init
    run
}

main
