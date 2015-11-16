#!/bin/bash

PIDFILE=$1
CONFIG=$2

if [ -z $PIDFILE ]; then
  echo "No PIDFILE given"
  exit 1
fi

if [ -n $CONFIG ]; then
  . $CONFIG 
else
  echo "No config exist at $CONFIG"
fi

$XP_INSTALL/bin/server.sh &

echo $! > $PIDFILE