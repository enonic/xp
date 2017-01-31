#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  export BRANCH=$TRAVIS_BRANCH;
else
  export BRANCH=PR-$TRAVIS_PULL_REQUEST;
fi

echo "Uploading $BRANCH"
