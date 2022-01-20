oc project red-hat-pay-monolith

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
oc new-app monolith -e JAVA_OPTS_APPEND="-Dspring.profiles.active=production -Dcom.sun.management.jmxremote.port=9091 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.autodiscovery=true -showversion -XX:StartFlightRecording=name=RedHatPayMonolith,settings=default,disk=true,filename=RedHatPayMonolithRecording.jfr,maxage=15m,dumponexit=true"
oc patch deploy monolith --type json -p '[{"op":"add","path":"/spec/template/spec/containers/0/ports/3","value":{"containerPort":9091}}]'
oc patch svc monolith --type json -p '[{"op":"add","path":"/spec/ports/3","value":{"name":"jfr-jmx","port":9091,"protocol":"TCP","targetPort":9091}}]'
oc expose svc monolith
cd ../environment/monolith/

oc set resources deploymentconfig postgres --limits=cpu=1,memory=1024Mi --requests=cpu=1,memory=1024Mi
oc set resources deployment monolith --limits=cpu=1,memory=1024Mi --requests=cpu=1,memory=1024Mi

