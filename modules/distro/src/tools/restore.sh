#!/bin/sh

REPOSITORY=$1

# Usage info
show_help() {
cat << EOF
Usage: ${0##*/} [-?|--help] -u USER:PASSWORD -r REPOSITORY -s SNAPSHOT_NAME  [-h HOSTNAME] [-p PORT] [-i true|false] [-n]

Stores a snapshot of the current state of the repository.
This will not include blobs and files in the repository.

	-?|--help			display this help and exit
	-u USER:PASSWORD		user:password for basic authentication
	-r REPOSITORY      The name of the repository to restore
	-s SNAPSHOT_NAME    The name of the snapshot to restore
	-n                  		enable nice format of output (requires python)

EOF
}

usageShort() {
echo "Usage: ${0##*/} [-?|--help] -u USER:PASSWORD -s SNAPSHOT_NAME [-h HOSTNAME] [-p PORT] [-n]"
}

PRETTY=""

# Parse arguments
while getopts '?u:h:p:r:s:n' OPTION
	do
		case $OPTION in
            u)
				uflag=1
				AUTH="$OPTARG"
			    ;;
            r)
				rflag=1
				REPOSITORY="$OPTARG"
				;;
            s)
				rflag=1
				SNAPSHOT="$OPTARG"
				;;
			p)
				pflag=1
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


if [[ -z $REPOSITORY ]]
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

JSON="{\"snapshotName\": \"$SNAPSHOT\", \"repository\" : \"$REPOSITORY\"}"

eval "curl -u $AUTH  -H \"Content-Type: application/json\" -XPOST 'http://$HOST:$PORT/admin/rest/repo/restore' -d '$JSON' | python -mjson.tool"


