# clean up
oc delete is,bc,deploy,svc -l app=httpd
oc delete infinispan example-infinispan
oc delete cache paymentcachedefinition
oc delete cache pointcachedefinition
oc delete cache tokencachedefinition
oc delete cache usercachedefinition
oc delete cache walletcachedefinition
oc delete batch put-schema
oc delete kafka my-cluster
oc delete kafkatopic point
oc delete kafkatopic payment
oc delete deploy payment
oc delete deploy point
oc delete svc payment
oc delete svc point
oc delete route payment
oc delete grafana example-grafana
oc delete sa infinispan-monitoring
oc delete grafanadatasource grafanadatasource
oc delete grafanadashboard simple-dashboard
oc delete cryostat cryostat-sample