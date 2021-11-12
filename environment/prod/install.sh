# RHDG デプロイ
oc new-app httpd~./artifacts --name=httpd
echo "sleep 60"
sleep 60
oc start-build httpd --from-dir=./artifacts
echo "sleep 60"
sleep 60

oc apply -f create_data_grid.yaml
#oc apply -f grafana_datasource.yaml
#oc apply -f put_schema.yaml

# Cache 作成
oc apply -f mycache.yaml








# Grafana
#oc apply -f service-account.yaml
#oc adm policy add-cluster-role-to-user cluster-monitoring-view -z infinispan-monitoring
#export TOKEN=`oc serviceaccounts get-token infinispan-monitoring`
#sed -e "s/__TOKEN__/${TOKEN}/g" grafana_datasource_template.yaml > grafana_datasource.yaml
#oc apply -f create_grafana.yaml

# clean up
#oc delete is,bc,deploy,svc -l app=httpd
#oc delete infinispan example-infinispan