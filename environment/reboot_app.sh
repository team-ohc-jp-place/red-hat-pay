set -e
oc scale deployment/payment --replicas=0 -n red-hat-pay
oc scale deployment/payment --replicas=1 -n red-hat-pay
