echo "Application(via Istio): http://`oc -n rhp-istio-system get route istio-ingressgateway -o jsonpath='{.spec.host}'`/index"
echo "Application(Direct)   : http://`oc get route payment -o jsonpath='{.spec.host}'`/index"
echo "Cryostat              : http://`oc get route cryostat-sample -o jsonpath='{.spec.host}'`"
echo "Infinispan Console    : http://`oc get route example-infinispan-console -o jsonpath='{.spec.host}'`/console/"
echo "OpenShift Logging     : http://`oc get route kibana -n openshift-logging -o jsonpath='{.spec.host}'`"

# Cryostatの認証情報
echo "Cryostat Credential"
CRYOSTAT_NAME=$(oc get cryostat -o jsonpath='{$.items[0].metadata.name}')
# Username
echo "User     : `oc get secret ${CRYOSTAT_NAME}-grafana-basic -o jsonpath='{$.data.GF_SECURITY_ADMIN_USER}' | base64 -d`"
# Password
echo "Password : `oc get secret ${CRYOSTAT_NAME}-grafana-basic -o jsonpath='{$.data.GF_SECURITY_ADMIN_PASSWORD}' | base64 -d`"

echo ""