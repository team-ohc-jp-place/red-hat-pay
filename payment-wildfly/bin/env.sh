#!/bin/bash

scriptDir=$(cd $(dirname $0) && pwd)

oc new-project wildfly-sample
oc apply -f ${scriptDir}/wildfly.yaml
sleep 10
oc exec wildfly-pod -- /opt/jboss/wildfly/bin/add-user.sh user1 secret --silent
