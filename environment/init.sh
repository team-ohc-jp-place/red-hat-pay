# create route for registry
oc patch configs.imageregistry.operator.openshift.io/cluster --type merge -p '{"spec":{"defaultRoute":true}}'

# install packages for building Red Hat Pay application
sudo yum install -y maven java-11-openjdk-devel

# Installing Podman
sudo yum install -y podman
