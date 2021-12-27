# change project
oc project red-hat-pay-monolith

# clean up resources
oc delete secret,svc,pvc,dc -l template=postgresql-persistent-template
oc delete bc monolith
oc delete all -l app=monolith
