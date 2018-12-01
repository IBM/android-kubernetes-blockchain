#!/bin/bash

if [ $# -ne 1 ]; then
    echo "usage: ./buildAndPushImages.sh [DOCKERHUB_USERNAME]"
    exit
fi

DOCKERHUB_USERNAME=$1

pushd containers/blockchain
export FABRIC_CFG_PATH=$(pwd)
./generate-certs.sh

echo "Building Docker images for blockchain network..."
docker build -t $DOCKERHUB_USERNAME/codepattern-orderer-peer:latest orderer/
docker build -t $DOCKERHUB_USERNAME/codepattern-shop-peer:latest shopPeer/
docker build -t $DOCKERHUB_USERNAME/codepattern-fitcoin-peer:latest fitcoinPeer/
docker build -t $DOCKERHUB_USERNAME/codepattern-shop-ca:latest shopCertificateAuthority/
docker build -t $DOCKERHUB_USERNAME/codepattern-fitcoin-ca:latest fitcoinCertificateAuthority/
docker build -t $DOCKERHUB_USERNAME/codepattern-blockchain-setup:latest blockchainNetwork/
docker build -t $DOCKERHUB_USERNAME/codepattern-backend:latest backend/
docker build -t $DOCKERHUB_USERNAME/codepattern-rabbitclient-api:latest rabbitClient/

echo "Pushing Docker images..."

docker push $DOCKERHUB_USERNAME/codepattern-orderer-peer:latest
docker push $DOCKERHUB_USERNAME/codepattern-shop-peer:latest
docker push $DOCKERHUB_USERNAME/codepattern-fitcoin-peer:latest
docker push $DOCKERHUB_USERNAME/codepattern-shop-ca:latest
docker push $DOCKERHUB_USERNAME/codepattern-fitcoin-ca:latest
docker push $DOCKERHUB_USERNAME/codepattern-blockchain-setup:latest
docker push $DOCKERHUB_USERNAME/codepattern-backend:latest
docker push $DOCKERHUB_USERNAME/codepattern-rabbitclient-api:latest

CONFIGJSON=$(cat configuration/config.json | base64)
CHANNELTX=$(cat configuration/channel.tx | base64)

pushd kube-configs

echo "Modifying yaml files..."

sed 's#CONFIG_JSON_BASE64#'$CONFIGJSON'#; s#CHANNEL_TX_BASE64#'$CHANNELTX#';' secrets.yaml > secrets.yaml.tmp
sed 's#DOCKERHUB_USERNAME#'$DOCKERHUB_USERNAME'#' blockchain-setup.yaml > blockchain-setup.yaml.tmp
sed 's#DOCKERHUB_USERNAME#'$DOCKERHUB_USERNAME'#' fitcoin-backend.yaml > fitcoin-backend.yaml.tmp
sed 's#DOCKERHUB_USERNAME#'$DOCKERHUB_USERNAME'#' fitcoin-ca.yaml > fitcoin-ca.yaml.tmp
sed 's#DOCKERHUB_USERNAME#'$DOCKERHUB_USERNAME'#' fitcoin-peer.yaml > fitcoin-peer.yaml.tmp
sed 's#DOCKERHUB_USERNAME#'$DOCKERHUB_USERNAME'#' orderer0.yaml > orderer0.yaml.tmp
sed 's#DOCKERHUB_USERNAME#'$DOCKERHUB_USERNAME'#' rabbitclient-api.yaml > rabbitclient-api.yaml.tmp
sed 's#DOCKERHUB_USERNAME#'$DOCKERHUB_USERNAME'#' shop-backend.yaml > shop-backend.yaml.tmp
sed 's#DOCKERHUB_USERNAME#'$DOCKERHUB_USERNAME'#' shop-ca.yaml > shop-ca.yaml.tmp
sed 's#DOCKERHUB_USERNAME#'$DOCKERHUB_USERNAME'#' shop-peer.yaml > shop-peer.yaml.tmp

mv secrets.yaml.tmp secrets.yaml
mv blockchain-setup.yaml.tmp blockchain-setup.yaml
mv fitcoin-backend.yaml.tmp fitcoin-backend.yaml
mv fitcoin-ca.yaml.tmp fitcoin-ca.yaml
mv fitcoin-peer.yaml.tmp fitcoin-peer.yaml
mv orderer0.yaml.tmp orderer0.yaml
mv rabbitclient-api.yaml.tmp rabbitclient-api.yaml
mv shop-backend.yaml.tmp shop-backend.yaml
mv shop-ca.yaml.tmp shop-ca.yaml
mv shop-peer.yaml.tmp shop-peer.yaml

popd
popd

pushd containers

echo "Building Docker images for nodejs microservices..."

docker build -t $DOCKERHUB_USERNAME/codepattern-leaderboard:latest leaderboard
docker build -t $DOCKERHUB_USERNAME/codepattern-mobile-assets:latest mobile-assets
docker build -t $DOCKERHUB_USERNAME/codepattern-registeree-api:latest registeree-api

echo "Pushing Docker images..."

docker push $DOCKERHUB_USERNAME/codepattern-leaderboard:latest
docker push $DOCKERHUB_USERNAME/codepattern-mobile-assets:latest
docker push $DOCKERHUB_USERNAME/codepattern-registeree-api:latest

sed 's#DOCKERHUB_USERNAME#'$DOCKERHUB_USERNAME'#' leaderboard-api.yaml > leaderboard-api.yaml.tmp
sed 's#DOCKERHUB_USERNAME#'$DOCKERHUB_USERNAME'#' mobile-assets.yaml > mobile-assets.yaml.tmp
sed 's#DOCKERHUB_USERNAME#'$DOCKERHUB_USERNAME'#' registeree-api.yaml > registeree-api.yaml.tmp

mv leaderboard-api.yaml.tmp leaderboard-api.yaml
mv mobile-assets.yaml.tmp mobile-assets.yaml
mv registeree-api.yaml.tmp registeree-api.yaml