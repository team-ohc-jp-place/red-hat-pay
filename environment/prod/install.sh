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

# AMQ Streams
oc apply -f create_kafka_cluster.yaml
oc apply -f create_kafka_topic.yaml

# Applications
oc apply -f deploy_app.yaml 

# Grafana
oc apply -f create_grafana.yaml
export TOKEN=`oc serviceaccounts get-token infinispan-monitoring`
sed -e "s/__TOKEN__/${TOKEN}/g" grafana_datasource_template.yaml > grafana_datasource.yaml
oc apply -f grafana_datasource.yaml
oc apply -f infinispan-operator-config.yaml
oc apply -f create_grafana_dashboard.yaml

# Cryostat
oc apply -f create_cryostat.yaml

# Service Mesh
oc apply -f servicemesh_controlplane.yaml
oc apply -f servicemesh_memberroll-default.yaml
oc apply -f servicemesh-gateway.yaml
oc apply -f servicemesh_destination-rule-all.yaml

# side car container injection
sleep 30
oc rollout restart deploy payment
oc rollout restart deploy point

echo ""
echo "Please access to http://`oc -n rhp-istio-system get route istio-ingressgateway -o jsonpath='{.spec.host}'`/index"

