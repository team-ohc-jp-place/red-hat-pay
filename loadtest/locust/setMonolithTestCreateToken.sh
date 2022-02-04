set -e

scriptDir=`dirname ${BASH_SOURCE[0]}`

bash ${scriptDir}/seed.sh ${scriptDir}/CreateToken.py http://`oc get route monolith -n red-hat-pay-monolith --template={{.spec.host}}`

