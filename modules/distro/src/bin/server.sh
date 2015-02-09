#!/bin/bash

# Add default JVM options here. You can also use JAVA_OPTS or XP_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS=""

# App variables
APP_NAME="EnonicXP"
APP_BASE_NAME=`basename "$0"`

warn ( ) {
    echo "$*"
}

die ( ) {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
darwin=false
case "`uname`" in
  Darwin* )
    darwin=true
    ;;
esac

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/.." >&-
APP_HOME="`pwd -P`"
cd "$SAVED" >&-

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME
        Please set the JAVA_HOME variable in your environment to match the
        location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
    Please set the JAVA_HOME variable in your environment to match the location of your Java installation."
fi

# For Darwin, add options to specify how the application appears in the dock
if $darwin; then
    XP_OPTS="$XP_OPTS \"-Xdock:name=$APP_NAME\" \"-Xdock:icon=$APP_HOME/media/logo.icns\""
fi

# Split up the JVM_OPTS And XP_OPTS values into an array, following the shell quoting and substitution rules
function splitJvmOpts() {
    JVM_OPTS=("$@")
}

XP_OPTS="$XP_OPTS -Dxp.install=$APP_HOME"

eval splitJvmOpts $DEFAULT_JVM_OPTS $JAVA_OPTS $XP_OPTS
exec "$JAVACMD" "${JVM_OPTS[@]}" -classpath "$APP_HOME/lib/*" com.enonic.xp.launcher.LauncherMain "$@"
