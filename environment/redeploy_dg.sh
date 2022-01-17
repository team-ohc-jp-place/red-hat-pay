oc delete is,bc,deploy,svc -l app=httpd
oc delete infinispan example-infinispan
oc delete cache paymentcachedefinition
oc delete cache pointcachedefinition
oc delete cache tokencachedefinition
oc delete cache usercachedefinition
oc delete cache walletcachedefinition
oc delete batch put-schema

./install_dg.sh
