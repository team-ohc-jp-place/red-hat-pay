#!/bin/bash

set -e

scriptDir=`dirname ${BASH_SOURCE[0]}`

testFile=""
hostName=""

export CURRENT_NS=`oc project -q`


# Display prompt if not arguments passed
if [[ -z "$1" && -z "$2" ]]
then
  read -p 'File to run: ' testFile
  read -p 'Host to attack: ' hostName
else
  testFile=$1
  hostName=$2
fi

# Confirmation
echo "Confirmation: file to run is: $testFile and the host: $hostName"

# Prepare Config map with new values
cat > ${scriptDir}/config-map.yaml << EOF1
kind: ConfigMap
apiVersion: v1
metadata:
  name: host-url
  namespace: locust
data:
  ATTACKED_HOST: $hostName
EOF1

# Push it to cluster
cat ${scriptDir}/config-map.yaml | oc apply -f -

# Clean after yourself
rm ${scriptDir}/config-map.yaml

# Prepare Config map with new values
cat > ${scriptDir}/config-map.yaml << EOF1
kind: ConfigMap
apiVersion: v1
metadata:
  name: script-file
  namespace: locust
data:
  locustfile.py: |
$(cat $testFile | sed 's/^/    /')
EOF1

# Push it to cluster
cat ${scriptDir}/config-map.yaml | oc apply -f -

# Clean after yourself
rm ${scriptDir}/config-map.yaml

# Update the environment variable to trigger a change
oc set env -n locust dc/locust-master --overwrite CONFIG_HASH=`date +%s%N`
oc set env -n locust dc/locust-slave --overwrite CONFIG_HASH=`date +%s%N`

oc project ${CURRENT_NS}