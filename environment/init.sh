set -e

cd `dirname $0`

initialCwd=`pwd -P`
scriptDir=`dirname ${BASH_SOURCE[0]}`

mvn install:install-file -Dfile=${scriptDir}/build/jfr4jdbc-driver-1.1.0.jar -DgroupId=dev.jfr4jdbc -DartifactId=jfr4jdbc-driver -Dversion=1.1.0 -Dpackaging=jar -DgeneratePom=true

# create route for registry
oc patch configs.imageregistry.operator.openshift.io/cluster --type merge -p '{"spec":{"defaultRoute":true}}'

# install packages for building Red Hat Pay application
sudo yum install -y maven java-11-openjdk-devel

# Installing Podman
sudo yum install -y podman

oc new-project red-hat-pay
oc new-project rhp-istio-system
oc new-project red-hat-pay-monolith

# Enabling monitoring for user-defined projects
oc project red-hat-pay
oc apply -f ${scriptDir}/monitoring/cluster-monitoring-config.yaml
oc apply -f ${scriptDir}/monitoring/service-account.yaml
oc adm policy add-cluster-role-to-user cluster-monitoring-view -z infinispan-monitoring
oc apply -f ${scriptDir}/monitoring/service_monitor.yaml
oc apply -f ${scriptDir}/logging/cluster_logging.yaml

# Kernel parameters
oc apply -f ${scriptDir}/node/tuning_kernel_parameter.yaml


echo "Please install some operators such as Data Grid, Cryostat on OpenShift Console"