#!/bin/bash

waitForPodWithSelector() {
  POD_SELECTOR=$1
  POD_NAME="$(kubectl get pods --selector="$POD_SELECTOR" -o name)"
  POD_STATUS="$(kubectl get pods --selector="$POD_SELECTOR" --output=jsonpath={.items..phase})"
  while [ "${POD_STATUS}" != "Running" ]; do
    echo "Wating for $POD_NAME to run."
    sleep 5;
    echo "Status of $POD_NAME is $POD_STATUS"
    if [ "${POD_STATUS}" == "Error" ]; then
        echo "There is an error in the pod. Please check logs."
        exit 1
    fi
    POD_STATUS="$(kubectl get pods --selector="$POD_SELECTOR" --output=jsonpath={.items..phase})"
  done
}

pushd containers/blockchain/kube-configs

echo "Creating persistent volume claims..."
kubectl create -f persistent-volume/persistent-volume-claims.yaml

echo "Waiting for volumes to get provisioned..."
PV_COUNT=$(kubectl get pvc | grep -c Bound)

while [ "${PV_COUNT}" != "4" ]; do
  echo "Wating for volumes to get provisioned."
  sleep 5;
  kubectl get pv,pvc
  PV_COUNT=$(kubectl get pvc | grep -c Bound)
done

## deploy docker here
kubectl apply -f docker-deployment.yaml

waitForPodWithSelector "name=docker"

kubectl create -f secrets.yaml
kubectl apply -f shop-ca.yaml
kubectl apply -f fitcoin-ca.yaml

waitForPodWithSelector "app=shop-ca"
waitForPodWithSelector "app=fitcoin-ca"

kubectl apply -f ca-datastore.yaml
kubectl apply -f fitcoin-statedb.yaml
kubectl apply -f shop-statedb.yaml

waitForPodWithSelector "app=ca-datastore"
waitForPodWithSelector "app=fitcoin-statedb"
waitForPodWithSelector "app=shop-statedb"

kubectl apply -f orderer0.yaml
kubectl apply -f shop-peer.yaml
kubectl apply -f fitcoin-peer.yaml

waitForPodWithSelector "app=orderer0"
waitForPodWithSelector "app=shop-peer"
waitForPodWithSelector "app=fitcoin-peer"

kubectl apply -f blockchain-setup.yaml

waitForPodWithSelector "app=blockchain-setup"

BLOCKCHAIN_SETUP=$(kubectl logs blockchain-setup-pod | grep -c "setup complete.")

while [ $BLOCKCHAIN_SETUP == "0" ]; do
  echo "Setting up blockchain..."
  sleep 5;
  echo "Blockchain setup pod status:"
  kubectl get pod blockchain-setup-pod
  BLOCKCHAIN_SETUP=$(kubectl logs blockchain-setup-pod | grep -c "setup complete.")
done

if [ $BLOCKCHAIN_SETUP != "0" ]; then
  echo "Blockchain setup is complete. Showing logs..."
  kubectl logs blockchain-setup-pod
fi

## Deploy backend

kubectl apply -f shop-backend.yaml
kubectl apply -f fitcoin-backend.yaml
kubectl apply -f rabbitclient-api.yaml

waitForPodWithSelector "app=shop-backend"
waitForPodWithSelector "app=fitcoin-backend"
waitForPodWithSelector "app=rabbitclient-api"

## deploy nodejs microservices
popd
pushd containers

kubectl apply -f leaderboard-api.yaml
kubectl apply -f mobile-assets.yaml
kubectl apply -f registeree-api.yaml

popd

waitForPodWithSelector "app=leaderboard-api"
waitForPodWithSelector "app=mobile-assets"
waitForPodWithSelector "app=registeree-api"

echo "Kubernetes deployments done: "

kubectl get svc,deployments