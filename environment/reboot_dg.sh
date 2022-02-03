set -e

oc delete infinispan example-infinispan
oc delete cache paymentcachedefinition
oc delete cache pointcachedefinition
oc delete cache tokencachedefinition
oc delete cache usercachedefinition
oc delete cache walletcachedefinition
oc delete batch put-schema

scriptDir=`dirname ${BASH_SOURCE[0]}`

oc apply -f ${scriptDir}/prod/datagrid/create_data_grid.yaml
oc apply -f ${scriptDir}/prod/datagrid/mycache.yaml
echo "sleep 60"
sleep 60
oc apply -f ${scriptDir}/prod/datagrid/put_schema.yaml

oc patch statefulset example-infinispan --type='json' -p='[{"op": "add", "path": "/spec/template/spec/containers/0/ports/-", "value": {"name": "jfr-jmx", "containerPort": 9091, "protocol": "TCP"} }]'
