#!/bin/bash

# Usage info
show_help() {
cat << EOF
Usage: ${0##*/} [-?|--help] -u USER:PASSWORD -r REPOSITORY -b BRANCHES [-i] [-h HOSTNAME] [-p PORT] [-n]

Reindex content in search indices for the given repository and branches.

	-?|--help			display this help and exit
	-u USER:PASSWORD		user:password for basic authentication
	-r REPOSITORY      		the name of the repository to reindex
	-b BRANCHES         		a comma-separated list of branches to be reindexed
	-i                  		if flag -i given, the indices will be deleted before recreated
	-h HOSTNAME			hostname, defaults to localhost
	-p PORT				port, defaults to 8080
	-n                  		enable nice format of output (requires python)

EOF
}

usageShort() {
echo "Usage: ${0##*/} [-?|--help] -u USER:PASSWORD -r REPOSITORY -b BRANCHES [-i] [-h HOSTNAME] [-p PORT] [-n]"
}

PRETTY=""
INITIALIZE="false"

# Parse arguments
while getopts '?u:h:p:r:nb:i' OPTION
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
			p)
				pflag=1
				PORT="$OPTARG"
				;;
            h)
				hflag=1
				HOST="$OPTARG"
				;;
            n)
            	nflag=1
                PRETTY="| python -mjson.tool"
                ;;
            i)
            	iflag=1
                INITIALIZE="true"
                ;;
            b)
            	bflag=1
                BRANCHES="$OPTARG"
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

if [[ -z $bflag ]]
then
     usageShort
     exit 1
fi

if [[ -z $INITIALIZE ]]
then
    usageShort
    exit 1
fi

if [[ -z $AUTH ]]
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


IFS=';' read -ra BRANCH_ARRAY <<< "$BRANCHES"

# @JsonProperty("repository") final String repositoryId, //
#                               @JsonProperty("initialize") final boolean initialize,  //
#                              @JsonProperty("branches") final List<String> branches )
#
#

JSON="{\"repository\": \"$REPOSITORY\", \"branches\": \"$BRANCHES\", \"initialize\" : $INITIALIZE}";

#echo "curl -u $AUTH -H \"Content-Type: application/json\" -XPOST 'http://$HOST:$PORT/admin/rest/repo/reindex' -d '$JSON' $PRETTY"
eval "curl -u $AUTH -H \"Content-Type: application/json\" -XPOST 'http://$HOST:$PORT/admin/rest/repo/reindex' -d '$JSON' $PRETTY"
echo
