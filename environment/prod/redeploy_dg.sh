oc delete is,bc,deploy,svc -l app=httpd
oc delete infinispan example-infinispan
oc delete cache paymentcachedefinition
oc delete cache pointcachedefinition
oc delete cache tokencachedefinition
oc delete cache usercachedefinition
oc delete cache walletcachedefinition
oc delete batch put-schema

# DataGrid
rm -f ./artifacts/*.jar
cp ../../cache/target/cache-1.0-SNAPSHOT.jar ./artifacts/
cp ../../domain/target/domain-1.0-SNAPSHOT.jar ./artifacts/

oc new-app httpd~./artifacts --name=httpd
echo "sleep 60"
sleep 60
oc start-build httpd --from-dir=./artifacts
echo "sleep 60"
sleep 60

oc apply -f create_data_grid.yaml
oc apply -f mycache.yaml
echo "sleep 60"
sleep 60
oc apply -f put_schema.yaml
