# change project
oc project red-hat-pay

# clean up resources
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
oc delete grafanadatasource grafanadatasource
oc delete grafanadashboard simple-dashboard
oc delete cryostat cryostat-sample
oc delete smcp basic -n rhp-istio-system
oc delete smmr default -n rhp-istio-system
oc delete gateway redhatpay-gateway
oc delete virtualservice redhatpay
oc delete destinationrule payment