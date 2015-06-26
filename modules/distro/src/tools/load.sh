#!/bin/bash

REPOSITORY=$1

# Usage info
show_help() {
cat << EOF
Usage: ${0##*/} [-?|--help] -u USER:PASSWORD -s SOURCEPATH [-h HOSTNAME] [-p PORT] [-i true|false] [-n]

Stores a snapshot of the current state of the repository.
This will not include blobs and files in the repository.

	-?|--help			display this help and exit
	-u USER:PASSWORD	user:password for basic authentication
	-s SOURCEPATH     	Source path
	-n                  enable nice format of output (requires python)

EOF
}

usageShort() {
echo "Usage: ${0##*/} [-?|--help] -u USER:PASSWORD -t SOURCEPATH [-h HOSTNAME] [-p PORT] [-i true|false] [-n]"
}

PRETTY=""

# Parse arguments
while getopts '?u:h:p:r:b:s:n' OPTION
	do
		case $OPTION in
            u)
				uflag=1
				AUTH="$OPTARG"
			    ;;
            s)
				SOURCE="$OPTARG"
				;;
			p)		
				PORT="$OPTARG"
				;;
            h)
				hflag=1
				HOST="$OPTARG"
				;;
            n)
                PRETTY="| python -mjson.tool"
                ;;
	  		\?)
		    	show_help >&2
		        exit 1
				;;
	  	esac
	done

shift $(($OPTIND - 1))

if [[ -z $SOURCE ]]
then
     usageShort
     exit 1
fi

if [[ -z $HOST ]]
then
     HOST="localhost"
fi

if [[ -z $PORT ]]
then
     PORT="8080"
fi

if [[ -z $AUTH ]]
then
     usageShort
     exit 1
fi

JSON="{\"sourceDirectory\" : \"$SOURCE\"}}"

echo "curl -u $AUTH  -H \"Content-Type: application/json\" -XPOST 'http://$HOST:$PORT/admin/rest/system/load' -d '$JSON' | python -mjson.tool"
eval "curl -u $AUTH  -H \"Content-Type: application/json\" -XPOST 'http://$HOST:$PORT/admin/rest/system/load' -d '$JSON' | python -mjson.tool"


