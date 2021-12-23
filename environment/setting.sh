export RED_HAT_PAY_PROJECT=red-hat-pay
export RED_HAT_PAY_USER=opentlc-mgr
export RED_HAT_PAY_PASSWORD=r3dh4t1!

# Login to OpenShift
oc login -u ${RED_HAT_PAY_USER} -p ${RED_HAT_PAY_PASSWORD}
oc project ${RED_HAT_PAY_PROJECT}
