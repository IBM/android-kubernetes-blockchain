Create IBM Cloud services

```
git clone <this-repo?>
cd containers/blockchain
```

* Compose for RabbitMQ https://console.bluemix.net/catalog/services/compose-for-rabbitmq
* Compose for Redis https://console.bluemix.net/catalog/services/compose-for-redis

Edit `configuration/config.js` and modify values for `rabbitmq` and `redisUrl`

```
chaincodePath: 'bcfit',
rabbitmq: 'amqps://admin:QWERTY@portal-ssl334-23.bmix-dal-yp-abc10717-6f73-4f63-b039-a1d2485c1566.devadvo-us-ibm-com.composedb.com:38919/bmix-dal-yp-abc10717-6f73-4f63-b039-a1d2485c1566',
redisUrl: 'redis://admin:QWERTY@sl-us-south-1-portal.23.dblayer.com:38916',
iotDashUrl: 'https://think-iot-processor.mybluemix.net/steps?message=',
```


Generate certs
```
export FABRIC_CFG_PATH=$(pwd)
./generate-certs.sh
```

Build the docker images
```
export DOCKERHUB_USERNAME=<your-dockerhub-username>

docker build -t $DOCKERHUB_USERNAME/kubecon-orderer-peer:latest orderer/
docker build -t $DOCKERHUB_USERNAME/kubecon-shop-peer:latest shopPeer/
docker build -t $DOCKERHUB_USERNAME/kubecon-fitcoin-peer:latest fitcoinPeer/
docker build -t $DOCKERHUB_USERNAME/kubecon-shop-ca:latest shopCertificateAuthority/
docker build -t $DOCKERHUB_USERNAME/kubecon-fitcoin-ca:latest fitcoinCertificateAuthority/
docker build -t $DOCKERHUB_USERNAME/kubecon-blockchain-setup:latest blockchainNetwork/
docker build -t $DOCKERHUB_USERNAME/kubecon-backend:latest backend/
docker build -t $DOCKERHUB_USERNAME/kubecon-rabbitclient-api:latest rabbitClient/

docker push $DOCKERHUB_USERNAME/kubecon-orderer-peer:latest
docker push $DOCKERHUB_USERNAME/kubecon-shop-peer:latest
docker push $DOCKERHUB_USERNAME/kubecon-fitcoin-peer:latest
docker push $DOCKERHUB_USERNAME/kubecon-shop-ca:latest
docker push $DOCKERHUB_USERNAME/kubecon-fitcoin-ca:latest
docker push $DOCKERHUB_USERNAME/kubecon-blockchain-setup:latest
docker push $DOCKERHUB_USERNAME/kubecon-backend:latest
docker push $DOCKERHUB_USERNAME/kubecon-rabbitclient-api:latest
```

THEN edit yaml files in here to use your images
* blockchain-setup.yaml
* orderer0.yaml
* shop-peer.yaml
* fitcoin-peer.yaml
* shop-ca.yaml
* fitcoin-ca.yaml
* fitcoin-backend.yaml
* rabbitclient-api.yaml

Encode `config.json` and `channel.tx` and modify `secrets.yaml`
```
cat configuration/config.json | base64

cat configuration/channel.tx | base64
```

Change directory

cd kube-configs

Create persistent volumes


```
kubectl create -f persistent-volume

# this uses dynamic provisioning using ibmc-file-gold storage
# for production, use static persistent volumes with your own NFS file storage
```

Create secrets and certificate authorities

```
kubectl create -f secrets.yaml

kubectl apply -f shop-ca.yaml
kubectl apply -f fitcoin-ca.yaml
```

create couchdbs
```
kubectl apply -f ca-datastore.yaml
kubectl apply -f fitcoin-statedb.yaml
kubectl apply -f shop-statedb.yaml
```

create orderer and peers
```
kubectl apply -f orderer0.yaml

kubectl apply -f shop-peer.yaml
kubectl apply -f fitcoin-peer.yaml
```

create blockchain-setup job. This creates and join peers in a channel _(mychannel)_ and installs and instantiate chaincode _(bcfit)_ with version 1
```
kubectl apply -f blockchain-setup.yaml
```

Check if blockchain setup is complete by checking log of blockchain-setup pod:

```
kubectl logs -l app=blockchain-setup

Default channel not found, attempting creation...
Successfully created a new default channel.
Joining peers to the default channel.
Chaincode is not installed, attempting installation...
Base container image present.
info: [packager/Golang.js]: packaging GOLANG from bcfit
info: [packager/Golang.js]: packaging GOLANG from bcfit
Successfully installed chaincode on the default channel.
Successfully instantiated chaincode on all peers.
Blockchain newtork setup complete.
```

If you see `Blockchain newtork setup complete.`, create the backend and rabbitclient-api

```
kubectl apply -f shop-backend.yaml
kubectl apply -f fitcoin-backend.yaml
kubectl apply -f rabbitclient-api.yaml
```

Check the external IP of rabbitclient-api:

```
kubectl get svc rabbitclient-api

NAME               CLUSTER-IP      EXTERNAL-IP     PORT(S)          AGE
rabbitclient-api   172.21.40.201   169.61.17.000   3000:30726/TCP   14m
```

Do a test enroll:
```
curl -H "Content-Type: application/json" -X POST -d '{"type":"enroll","queue":"user_queue","params":{}}' "http://169.61.17.000:3000/api/execute"

{"status":"success","resultId":"7f90764a-8660-45f2-904d-47d8fb87a900"}
```

check the results using the `resultId` you from the step above

```
curl http://169.61.17.170:3000/api/results/7f90764a-8660-45f2-904d-47d8fb87a900

{"status":"done","result":"{\"message\":\"success\",\"result\":{\"user\":\"72fe3ebd-8fb8-4974-8368-ae68e42ae49f\",\"txId\":\"75e523ae7cadefdfc9248d6df6be0c024bb8f1620ac6bcbfe52350cbfe55c4d0\"}}"}
```

```
kubectl scale deploy/fitcoin-backend-deployment --replicas=10
```
