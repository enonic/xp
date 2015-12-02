#!/bin/bash

PIDFILE=$1
CONFIG=$2

if [ -z $PIDFILE ]; then
  echo "No PIDFILE given"
  exit 1
fi

if [ -f $CONFIG ]; then
  . $CONFIG 
else
  echo "No config exist at $CONFIG"
fi

check_java()
{

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

    if [[ "$JAVACMD" ]]; then
        version=$(${JAVACMD} -version 2>&1 | awk -F '"' '/version/ {print $2}')
        echo "Java JDK version: $version"
        if [[ "$version" < "1.8" ]]; then
            echo "Needs Java JDK 1.8+ to run"
            exit 1
        fi
    fi

}

start()
{
    ${XP_INSTALL}/bin/server.sh &

    PID=$!
    STATUS=$?

    if (($STATUS > 0)); then
        printf '%s\n' 'cannot start server' >&2
        exit 1
    fi

    echo ${PID} > ${PIDFILE}
}

check_java
start
