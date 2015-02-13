#!/bin/bash

# Usage info
show_help() {
cat << EOF
Usage: ${0##*/} -u USER:PASSWORD  -t TARGET -s SOURCE [-h HOSTNAME] [-p PORT] [-i TRUE/FALSE]

Export node from a branch in a repository

	-?				display this help and exit
	-u USER:PASSWORD		user:password for basic authentication
	-t TARGET			target file path to save export
	-s SOURCE			path of data to export. Format: <repo-name>:<branch-name>:<node-path>. Sample: 'cms-repo:stage:/content'
	-h HOSTNAME			hostname, defaults to localhost
	-p PORT				port, defaults to 8080

EOF
}

usageShort() {
echo "Usage: ./${0##*/} -? -u USER:PASSWORD  -t TARGET -s SOURCE [-h HOSTNAME] [-p PORT]"
}

# Parse arguments
while getopts '?u:h:p:t:s:i:' OPTION
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
			i)
				pflag=1
				PORT="$OPTARG"
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

if [[ -z $HOSTNAME ]]
then
     HOSTNAME="localhost"
fi

if [[ -z $PORT ]]
then
     PORT="8080"
fi


JSON="{\"sourceRepoPath\": \"$SOURCE\", \"targetDirectory\": \"$TARGETPATH\", \"importWithIds\": $INCLUDEIDS}"

eval "curl -u $AUTH -H \"Content-Type: application/json\" -XPOST 'http://$HOSTNAME:$PORT/admin/rest/export/export' -d '$JSON' | python -mjson.tool"