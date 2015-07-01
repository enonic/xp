#!/bin/sh

DIRNAME=`dirname "$0"`
PROGNAME=`basename "$0"`
ARG1=$1
ARGS=$@

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
    DEFAULT_JAVA_OPTS="-Xms1024M -Xmx2048M"
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

init() {
    detectOS
    locateJava
    setupDefaults
    setupOptions
    setupDebugOptions
}

run() {
    exec "$JAVACMD" $JAVA_OPTS -classpath "$DIRNAME/lib/*" com.enonic.xp.toolbox.Main $ARGS
}

main() {
    init
    run
}

main
