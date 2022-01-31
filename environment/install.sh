set -e

initialCwd=`pwd -P`
scriptDir=`dirname ${BASH_SOURCE[0]}`

# DataGrid
bash ${scriptDir}/install_dg.sh

# JFR for Infinispan
oc apply -f ${scriptDir}/prod/cryostat/enable_jfr_data_grid.yaml

# AMQ Streams
oc apply -f ${scriptDir}/prod/kafka/create_kafka_cluster.yaml
oc apply -f ${scriptDir}/prod/kafka/create_kafka_topic.yaml

# Applications
oc apply -f ${scriptDir}/prod/app/deploy_app.yaml

# Grafana
oc apply -f ${scriptDir}/prod/monitoring/create_grafana.yaml
export TOKEN=`oc serviceaccounts get-token infinispan-monitoring`
sed -e "s/__TOKEN__/${TOKEN}/g" ${scriptDir}/prod/monitoring/grafana_datasource_template.yaml > ${scriptDir}/prod/monitoring/grafana_datasource.yaml
oc apply -f ${scriptDir}/prod/monitoring/grafana_datasource.yaml
oc apply -f ${scriptDir}/prod/monitoring/infinispan-operator-config.yaml
oc apply -f ${scriptDir}/prod/monitoring/create_grafana_dashboard.yaml

# Cryostat
oc apply -f ${scriptDir}/prod/cryostat/create_cryostat.yaml

# Service Mesh
# oc apply -f ${scriptDir}/prod/servicemesh/servicemesh_controlplane.yaml
# oc apply -f ${scriptDir}/prod/servicemesh/servicemesh_memberroll-default.yaml
# oc apply -f ${scriptDir}/prod/servicemesh/servicemesh-gateway.yaml
# oc apply -f ${scriptDir}/prod/servicemesh/servicemesh_destination-rule-all.yaml

# OpenShift Logging
oc apply -f ${scriptDir}/prod/logging/cluster_logging.yaml

# side car container injection
sleep 30
oc rollout restart deploy payment
oc rollout restart deploy point

echo ""
bash ${scriptDir}/showURL.sh