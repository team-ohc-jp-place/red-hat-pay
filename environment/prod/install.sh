oc apply -f create_data_grid.yaml
oc apply -f grafana_datasource.yaml
oc apply -f put_schema.yaml

oc apply -f datagrid-libs.yaml
oc apply -f datagrid-libs-pod.yaml

oc cp --no-preserve=true libs datagrid-libs-pod:/

# Grafana
#oc apply -f service-account.yaml
#oc adm policy add-cluster-role-to-user cluster-monitoring-view -z infinispan-monitoring
#export TOKEN=`oc serviceaccounts get-token infinispan-monitoring`
#sed -e "s/__TOKEN__/${TOKEN}/g" grafana_datasource_template.yaml > grafana_datasource.yaml
#oc apply -f create_grafana.yaml
