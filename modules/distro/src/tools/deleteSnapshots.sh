#!/bin/sh


# Usage info
show_help() {
cat << EOF
Usage: ${0##*/} [-?|--help] -b TIMESTAMP -u USER:PASSWORD [-h HOSTNAME] [-p PORT] [-i true|false] [-n]

Deletes snapshots, either before a given timestamp or by name

	-?|--help			display this help and exit
	-b TIMESTAMP            all snaphots before (not including) this timestamp will be deleted
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
while getopts '?u:h:p:b:n' OPTION
	do
		case $OPTION in
            u)
				uflag=1
				AUTH="$OPTARG"
			    ;;
            b)
				uflag=1
				BEFORE="$OPTARG"
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

if [[ -z $BEFORE ]]
then
     usageShort
     exit 1
fi

JSON="{\"snapshotNames\": [], \"before\" : \"$BEFORE\"}"

eval "curl -u $AUTH -H \"Content-Type: application/json\" -XPOST 'http://localhost:8080/admin/rest/repo/delete' -d '$JSON' | python -mjson.tool"


