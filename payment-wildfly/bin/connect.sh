#!/bin/bash

oc port-forward wildfly-pod 8080:8080 9990:9990
