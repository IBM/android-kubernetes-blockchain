#!/bin/bash
export FABRIC_CFG_PATH=$PWD
# sh ./clean.sh
sh ./generate-certs.sh
sh ./docker-images.sh
docker-compose -p "fitcoin" up -d fitcoin-peer
sleep 20s
docker-compose -p "fitcoin" up -d blockchain-setup
sleep 30s
docker-compose -p "fitcoin" up -d rabbitmq
sleep 50s
docker exec rabbitmq1 /bin/sh -c "rabbitmqctl set_policy ha-all '.' \"{'ha-mode':'all','ha-sync-mode':'automatic'}\""
sleep 10s
docker-compose -p "fitcoin" up -d
sleep 1s
docker-compose -p "fitcoin" up -d --scale fitcoin-backend=2
sleep 1s
docker-compose -p "fitcoin" up -d --scale fitcoin-backend=3
sleep 1s
docker-compose -p "fitcoin" up -d --scale fitcoin-backend=4
sleep 1s
docker-compose -p "fitcoin" up -d --scale fitcoin-backend=5
sleep 1s
docker ps
