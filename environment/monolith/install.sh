# Login to OpenShift
oc login -u opentlc-mgr -p r3dh4t1!
oc project user2-project

# PostgreSQL
oc process postgresql-persistent -n openshift -p DATABASE_SERVICE_NAME=postgres -p POSTGRESQL_USER=postgres -p POSTGRESQL_PASSWORD=postgres -p POSTGRESQL_DATABASE=rhpay | oc create -f -
echo "sleep 60"
sleep 60
oc rsh $(oc get po -l name=postgres -oname) createdb rhpay -O postgres

# Application
cd ../../monolith/
rm -rf ocp/deployments/*.jar
cp target/*.jar ocp/deployments/
oc new-build --binary=true --name=monolith --image-stream=ubi8-openjdk-11:1.3
oc start-build monolith --from-dir=./ocp --follow
oc new-app monolith -e JAVA_OPTS_APPEND="-Dspring.profiles.active=production"
oc expose svc monolith
cd ../environment/monolith/

# clean up
#oc delete secret,svc,pvc,dc -l template=postgresql-persistent-template
#oc delete bc monolith
#oc delete all -l app=monolith
