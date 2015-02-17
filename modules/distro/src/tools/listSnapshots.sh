#!/bin/bash

# Usage info
show_help() {
cat << EOF
Usage: ${0##*/} [-?|--help] -u USER:PASSWORD [-h HOSTNAME] [-p PORT] [-i true|false] [-n]

Returns a list of existing snapshots with name and status

	-?|--help			display this help and exit
	-u USER:PASSWORD		user:password for basic authentication
	-n                  		enable nice format of output (requires python)
	-h HOSTNAME			hostname, defaults to localhost
	-p PORT				port, defaults to 8080

EOF
}

usageShort() {
echo "Usage: ${0##*/} [-?|--help] -u USER:PASSWORD [-h HOSTNAME] [-p PORT] [-n]"
}

PRETTY=""

# Parse arguments
while getopts '?u:h:p:n' OPTION
	do
		case $OPTION in
            u)
				uflag=1
				AUTH="$OPTARG"
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


eval "curl -u $AUTH -XGET 'http://$HOST:$PORT/admin/rest/repo/list' | python -mjson.tool"
