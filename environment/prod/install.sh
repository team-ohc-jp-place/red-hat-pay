# Deploying DataGrid
oc new-app httpd~./artifacts --name=httpd
echo "sleep 60"
sleep 60
oc start-build httpd --from-dir=./artifacts
echo "sleep 60"
sleep 60

oc apply -f create_data_grid.yaml
#oc apply -f grafana_datasource.yaml
#oc apply -f put_schema.yaml
oc apply -f mycache.yaml

# Deploying AMQ Streams cluster and topics
oc apply -f create_kafka_cluster.yaml
oc apply -f create_kafka_topic.yaml

# Deploying Applications
oc apply -f deploy_app.yaml 

# Enabling monitoring for user-defined projects
oc apply -f cluster-monitoring-config.yaml


# Grafana
#oc apply -f service-account.yaml
#oc adm policy add-cluster-role-to-user cluster-monitoring-view -z infinispan-monitoring
#export TOKEN=`oc serviceaccounts get-token infinispan-monitoring`
#sed -e "s/__TOKEN__/${TOKEN}/g" grafana_datasource_template.yaml > grafana_datasource.yaml
#oc apply -f create_grafana.yaml

# clean up
#oc delete is,bc,deploy,svc -l app=httpd
#oc delete infinispan example-infinispan
#oc delete cache paymentcachedefinition
#oc delete cache pointcachedefinition
#oc delete cache tokencachedefinition
#oc delete cache usercachedefinition
#oc delete cache walletcachedefinition
#oc delete kafka my-cluster
#oc delete kafkatopic point
#oc delete kafkatopic payment
#oc delete deploy payment
#oc delete deploy point
#oc delete svc payment
#oc delete svc point
#oc delete route payment