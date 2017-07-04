#!/usr/bin/env bash

[ $# -eq 0 ] && { echo "Usage: $0 \"text\" [dims [module]]"; exit 1; }

URL=http://sample-env.2riifp3zdg.eu-central-1.elasticbeanstalk.com/parse
# MODULE="ro\$core"

# [ $# -gt 2 ] && { MODULE=$3; }

# if [ $# -gt 1 ]; then
#    curl -X POST $URL --data '{"text":"$1", "module":"$MODULE", "dims":"$2"}' --header "Content-type:application/json"
# else
#   curl -X POST $URL --data '{"text":"$1", "module":"$MODULE"}' --header "Content-type:application/json"
# fi

curl -X POST $URL --data '{"text":"$1"}' --header "Content-type:application/json"
