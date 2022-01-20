scriptDir=`dirname ${BASH_SOURCE[0]}`

mkdir ${scriptDir}/artifacts
cp ${scriptDir}/../cache/target/cache-*.jar ./artifacts/
cp ${scriptDir}/../domain/target/domain-*.jar ./artifacts/

oc new-app httpd~${scriptDir}/artifacts --name=httpd
echo "sleep 60"
sleep 60
oc start-build httpd --from-dir=./artifacts
oc expose svc httpd

rm -fr ${scriptDir}/artifacts

httpReady=403
url=`oc get route httpd -o jsonpath='{.spec.host}'`
until [ ${httpReady} -eq 200 ]
do
   sleep 1
   httpReady=`curl -LI ${url}/cache-1.0-SNAPSHOT.jar -o /dev/null  -w '%{http_code}\n' -s`
done

oc apply -f ${scriptDir}/prod/datagrid/create_data_grid.yaml
oc apply -f ${scriptDir}/prod/datagrid/mycache.yaml
echo "sleep 60"
sleep 60
oc apply -f ${scriptDir}/prod/datagrid/put_schema.yaml

oc patch statefulset example-infinispan --type='json' -p='[{"op": "add", "path": "/spec/template/spec/containers/0/ports/-", "value": {"name": "jfr-jmx", "containerPort": 9091, "protocol": "TCP"} }]'
