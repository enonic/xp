#!/bin/sh

DIRNAME=`dirname "$0"`
PROGNAME=`basename "$0"`
ARG1=$1
ARGS=$@

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

detectOS() {
    cygwin=false;
    darwin=false;
    case "`uname`" in
        CYGWIN*)
            cygwin=true
            ;;
        Darwin*)
            darwin=true
            ;;
    esac
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
    DEFAULT_JAVA_OPTS="-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=60 -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark"
    DEFAULT_JAVA_DEBUG_OPTS="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
}

setupOptions() {
    if [ "x$JAVA_OPTS" = "x" ]; then
        JAVA_OPTS="$DEFAULT_JAVA_OPTS"
    fi
    export JAVA_OPTS
}

setupDebugOptions() {
    if [ "$ARG1" = "debug" ]; then
        if [ "x$JAVA_DEBUG_OPTS" = "x" ]; then
            JAVA_DEBUG_OPTS="$DEFAULT_JAVA_DEBUG_OPTS"
        fi

        JAVA_OPTS="$JAVA_DEBUG_OPTS $JAVA_OPTS"
    fi
}

locateInstallDir() {
    XP_INSTALL=`cd "$DIRNAME/.."; pwd`
    if [ ! -d "$XP_INSTALL" ]; then
        die "XP_INSTALL is not valid: $XP_INSTALL"
    fi
}

init() {
    detectOS
    locateJava
    setupDefaults
    setupOptions
    setupDebugOptions
    locateInstallDir
}

run() {
    exec "$JAVACMD" $JAVA_OPTS -Dxp.install="$XP_INSTALL" -Dfile.encoding=UTF8 $XP_OPTS -classpath "$XP_INSTALL/lib/*" com.enonic.xp.launcher.LauncherMain $ARGS
}

main() {
    init
    run
}

main
