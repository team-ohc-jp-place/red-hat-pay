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
oc apply -f cluster-monitoring-config.yaml
oc apply -f service-account.yaml
oc adm policy add-cluster-role-to-user cluster-monitoring-view -z infinispan-monitoring
oc apply -f service_monitor.yaml


echo "Please install some operators such as Data Grid, Cryostat on OpenShift Console"