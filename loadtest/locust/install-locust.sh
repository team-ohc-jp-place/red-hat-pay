#!/bin/bash

LOCUST_NAMESPACE="locust"

oc new-project $LOCUST_NAMESPACE
oc project $HLOCUST_NAMESPACE

oc process -f master-deployment.yaml | oc create -f -
oc process -f slave-deployment.yaml | oc create -f -
