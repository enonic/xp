#!/bin/bash

REPOSITORY=$1

# Usage info
show_help() {
cat << EOF
Usage: ${0##*/} [-?|--help] -u USER:PASSWORD -t TARGETPATH [-h HOSTNAME] [-p PORT] [-i true|false] [-n]

Stores a snapshot of the current state of the repository.
This will not include blobs and files in the repository.

	-?|--help			display this help and exit
	-u USER:PASSWORD	user:password for basic authentication
	-t TARGETPATH     	Target path
	-n                  enable nice format of output (requires python)

EOF
}

usageShort() {
echo "Usage: ${0##*/} [-?|--help] -u USER:PASSWORD -t TARGETPATH [-h HOSTNAME] [-p PORT] [-i true|false] [-n]"
}

PRETTY=""

# Parse arguments
while getopts '?u:h:p:b:t:n' OPTION
	do
		case $OPTION in
            u)
				uflag=1
				AUTH="$OPTARG"
			    ;;
            t)
				TARGET="$OPTARG"
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

if [[ -z $TARGET ]]
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

JSON="{\"targetDirectory\" : \"$TARGET\"}}"

echo "curl -u $AUTH  -H \"Content-Type: application/json\" -XPOST 'http://$HOST:$PORT/admin/rest/system/dump' -d '$JSON' | python -mjson.tool"
eval "curl -u $AUTH  -H \"Content-Type: application/json\" -XPOST 'http://$HOST:$PORT/admin/rest/system/dump' -d '$JSON' | python -mjson.tool"


