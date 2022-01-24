set -e

initialCwd=`pwd -P`
scriptDir=`dirname ${BASH_SOURCE[0]}`

# create route for registry
oc patch configs.imageregistry.operator.openshift.io/cluster --type merge -p '{"spec":{"defaultRoute":true}}'

# install packages for building Red Hat Pay application
sudo yum install -y maven java-11-openjdk-devel
sudo alternatives --set java java-11-openjdk.x86_64

# Installing Podman
sudo yum install -y podman

oc new-project red-hat-pay
oc new-project rhp-istio-system
oc new-project red-hat-pay-monolith

# Enabling monitoring for user-defined projects
oc project red-hat-pay
oc apply -f ${scriptDir}/prod/monitoring/cluster-monitoring-config.yaml
oc apply -f ${scriptDir}/prod/monitoring/service-account.yaml
oc adm policy add-cluster-role-to-user cluster-monitoring-view -z infinispan-monitoring
oc apply -f ${scriptDir}/prod/monitoring/service_monitor.yaml
oc apply -f ${scriptDir}/prod/logging/cluster_logging.yaml

# Kernel parameters
oc apply -f ${scriptDir}/prod/node/tuning_kernel_parameter.yaml

cd ${scriptDir}/build
mvn install:install-file -Dfile=jfr4jdbc-driver-1.1.0.jar -DgroupId=dev.jfr4jdbc -DartifactId=jfr4jdbc-driver -Dversion=1.1.0 -Dpackaging=jar -DgeneratePom=true

cd ${initialCwd}

echo "Please install some operators such as Data Grid, Cryostat, OpenShift Logging and Grafana(Community) on OpenShift Console"