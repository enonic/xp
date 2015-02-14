#!/bin/bash

# Usage info
show_help() {
cat << EOF
Usage: ${0##*/} [-?|--help] -u USER:PASSWORD -s SOURCE_REPO_PATH -t TARGET_DIR [-h HOSTNAME] [-p PORT] [-i true|false] [-n]

Export node from a branch in a repository

	-?|--help			display this help and exit
	-u USER:PASSWORD		user:password for basic authentication
	-t TARGET_DIR			target directory to save export
	-s SOURCE_REPO_PATH			path of data to export. Format: <repo-name>:<branch-name>:<node-path>.
						Sample: 'cms-repo:stage:/content'
	-h HOSTNAME			hostname, defaults to localhost
	-p PORT				port, defaults to 8080
	-n                  enable nice format of output (requires python)

EOF
}

usageShort() {
echo "Usage: ${0##*/} [-?|--help] -u USER:PASSWORD -s SOURCE_REPO_PATH -t TARGET_DIR [-h HOSTNAME] [-p PORT] [-i true|false] [-n]"
}

args=("$@")
for element in "${args[@]}"; do
	if [ "$element" == "--help" ]; then
		 show_help
            exit
	fi
done

PRETTY=""

# Parse arguments
while getopts '?u:h:p:t:s:i:n' OPTION
	do
		case $OPTION in
            u)
				uflag=1
				AUTH="$OPTARG"
			    ;;
			t)
				tflag=1
				TARGETPATH="$OPTARG"
				;;
			s)
				sflag=1
				SOURCE="$OPTARG"
				;;
			i)
				iflag=1
				INCLUDEIDS="$OPTARG"
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

if [[ -z $AUTH ]]
then
     usageShort
     exit 1
fi

if [[ -z $TARGETPATH ]]
then
     usageShort
     exit 1
fi

if [[ -z $SOURCE ]]
then
     usageShort
     exit 1
fi

if [[ -z $INCLUDEIDS ]]
then
	INCLUDEIDS=true
fi

if [[ -z $HOST ]]
then
     HOST="localhost"
fi

if [[ -z $PORT ]]
then
     PORT="8080"
fi



JSON="{\"sourceRepoPath\": \"$SOURCE\", \"targetDirectory\": \"$TARGETPATH\", \"importWithIds\": $INCLUDEIDS}"

eval "curl -u $AUTH -H \"Content-Type: application/json\" -XPOST 'http://$HOST:$PORT/admin/rest/export/export' -d '$JSON' $PRETTY"
