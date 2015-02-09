#!/bin/sh

#
# Handle specific scripts; the SCRIPT_NAME is exactly the name of the XP
# script; for example server.sh, server-debug.sh, ...
#
# if [ "$XP_SCRIPT" == "SCRIPT_NAME" ]; then
#   Actions go here...
# fi
#

#
# General settings which should be applied for all scripts go here; please keep
# in mind that it is possible that scripts might be executed more than once, e.g.
# in example of the start script where the start script is executed first and the
# XP script afterwards.
#

#
# The following section shows the possible configuration options for the default
# XP scripts.
#

# export JAVA_HOME           # Location of Java installation
# export JAVA_OPTS           # Java options
# export JAVA_DEBUG_OPTS     # Java debug options
# export XP_HOME             # Enonic XP home folder
# export XP_OPTS             # Additional available Enonic XP options
