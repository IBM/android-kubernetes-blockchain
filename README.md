# Create an Android app with Blockchain Integration

In this code pattern, we will create an Android app that tracks the user steps with Blockchain capabalities using Kubernetes. The users are registered to the Blockchain network anonymously and gets rewarded with some "coins" for the steps they take. The users can trade their coins for some swag and these transactions are executed in the Blockchain network. This Android app was used in KubeCon Europe 2018.

This code pattern is for developers who wish to provide data anonymity and security to their users. Their users will be more confident to use their app if it gives them more control over their privacy. The developers can also extend the pattern to use the backend from different platforms.

When you have completed this code pattern, you will understand how to:

* Build a native Android app and use Google Fit for its steps data
* Deploy a Blockchain Network in Kubernetes
* Integrate the Android app with the Blockchain network

## Flow

![](docs/architecture.png)

1. The REST API is how the mobile app will interact with the blockchain network. The API will acknowledge the request and the mobile app will receive a unique key (random numbers and letters) which will be used to get the blockchain’s response later.
2. The API just stores the request in a queue in RabbitMQ. The queue has 2 channels, which are for the user (Fitcoin org) and the seller (Shop org). The requests can either be:
    * User enrollment
    * Query data from the blockchain network (number of kubecoins of a user, products that are for sale, contracts, etc…)
    * Invoke or perform a transaction (send steps to receive kubecoins, claim a product, complete a transaction, etc…)
3. The execution workers use the Hyperledger Fabric Node.js SDK to perform above requests. They are listening to the requests from RabbitMQ.
4. The workers send the requests to the Blockchain network and are then processed. The blockchain network uses NFS to persist the ledger and state database.
5. The workers receive the response and then persists it in the redis database with the unique key from (# 1).
6. The mobile app will continue to wait for the blockchain’s response results that should be in the redis database. This is where the unique key is used. The mobile app will query the redis database with the unique key.
7. The Registration microservice is used to create a user or update the user’s steps. When the blockchain network enrolls a user, this service uses the user id assigned by the blockchain network.
8. When a user is registered, it calls a Cloud Function to generate a random name and avatar for the user.
9. The data from the microservices are persisted in a MongoDB. This is where the user’s data (steps, name and avatar) and mobile assets (booklet/articles in the first view of mobile app) are persisted.
10. The mobile assets microservice is used to query the MongoDB to get dynamic data for the mobile app. The booklet in the first view uses the database for its content.
11. The leaderboard microservice is used to get the standings of the users.


## Summary

The first time the user opens the app, he gets anonymously assigned a unique user ID in the Blockchain network. They also get assigned a random avatar and name which will be stored in MongoDB. This data will be used for the Leaderboard. The 3 microservices (Leaderboard, Mobile Assets, Registration) outside of the blockchain network are Node.js web apps to get data from the MonoDB. As users walk around, their steps will be sent to the blockchain network and they will be rewarded with "Kubecoins". These coins can be used in the blockchain network to trade assets. The assets we have are some swags(stickers, bandanas, etc.) for the KubeCon conference. Users can see and claim them using the app. Once they claim a product, they'll get a Contract ID. They'll show the Contract ID to the seller (us) and we will complete and verify the transaction in our dashboard and give them the swag. The users can also check how they are doing with their steps compared to other people in the Standings view in the mobile app or in the Kubecoin dashboard.

## Included Components

* [IBM Cloud Container Service](https://console.bluemix.net/docs/containers/container_index.html): IBM Bluemix Container Service manages highly available apps inside Docker containers and Kubernetes clusters on the IBM Cloud.
* [Hyperledger Fabric v1.0](https://www.hyperledger.org/projects/fabric): An implementation of blockchain technology that is intended as a foundation for developing blockchain applications or solutions for business.
* [Compose for MongoDB](https://www.ibm.com/cloud/compose/mongodb): MongoDB with its powerful indexing and querying, aggregation and wide driver support, has become the go-to JSON data store for many startups and enterprises.
* [Compose for RabbitMQ](https://www.ibm.com/cloud/compose/rabbitmq): RabbitMQ is a messaging broker that asynchronously communicates between your applications and databases, allowing you to keep seperation between your data and app layers.
* [Compose for Redis](https://www.ibm.com/cloud/compose/redis): An open-source, in-memory data structure store, used as a database, cache and message broker.
* [Cloud Functions](https://www.ibm.com/cloud/functions): Execute code on demand in a highly scalable, serverless environment.

## Featured Technologies

* [Blockchain](https://www.ibm.com/blockchain): Distributed database maintaining a continuously growing list of secured records or blocks.
* [Container Orchestration](https://www.ibm.com/cloud/container-service): Automating the deployment, scaling and management of containerized applications.
* [Serverless](https://www.ibm.com/cloud/functions): An event-action platform that allows you to execute code in response to an event.
* [Databases](https://en.wikipedia.org/wiki/IBM_Information_Management_System#.22Full_Function.22_databases): Repository for storing and managing collections of data.

# Prerequisite

Create a Kubernetes cluster with either [Minikube](https://kubernetes.io/docs/getting-started-guides/minikube) for local testing, or with [IBM Bluemix Container Service](https://console.bluemix.net/docs/containers/cs_cli_install.html#cs_cli_install) to deploy in cloud. The code here is regularly tested against [Kubernetes Cluster from Bluemix Container Service](https://console.ng.bluemix.net/docs/containers/cs_ov.html#cs_ov) using Travis.

Install [Docker](https://www.docker.com) by following the instructions [here](https://www.docker.com/community-edition#/download) for your preferrerd operating system. You would need docker if you want to build and use your own images.

Install [Android Studio](https://developer.android.com/studio/).

# Steps

### 1. Clone the repo

```
$ git clone https://github.com/IBM/android-kubernetes-blockchain
```

### 2. Create IBM Cloud services

Create the following services:

* [IBM Cloud Container Service](https://console.bluemix.net/containers-kubernetes/catalog/cluster)
* [Compose for MongoDB](https://console.bluemix.net/catalog/services/compose-for-mongodb)
* [Compose for RabbitMQ](https://console.bluemix.net/catalog/services/compose-for-rabbitmq)
* [Compose for Redis](https://console.bluemix.net/catalog/services/compose-for-redis)

### 3. Configure the Blockchain Network

* Get your Compose for RabbitMQ and Redis credentials in your IBM Cloud Dashboard

![](docs/rabbit-credentials.png)

![](docs/redis-credentials.png)

* In `containers/blockchain/configuration/config.js`, modify the values for `rabbitmq` and `redis` with your own credentials.

```
chaincodePath: 'bcfit',
rabbitmq: 'amqps://admin:QWERTY@portal-ssl334-23.bmix-dal-yp-abc10717-6f73-4f63-b039-a1d2485c1566.devadvo-us-ibm-com.composedb.com:38919/bmix-dal-yp-abc10717-6f73-4f63-b039-a1d2485c1566',
redisUrl: 'redis://admin:QWERTY@sl-us-south-1-portal.23.dblayer.com:38916',
```

* Generate certificates and for the blockchain network

```
$ cd containers/blockchain
$ export FABRIC_CFG_PATH=$(pwd)
$ ./generate-certs.sh
```

* Build the docker images for your blockchain network

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

* Edit these YAML files in `kube-configs` folder to use your images
  * blockchain-setup.yaml
  * orderer0.yaml
  * shop-peer.yaml
  * fitcoin-peer.yaml
  * shop-ca.yaml
  * fitcoin-ca.yaml
  * fitcoin-backend.yaml
  * rabbitclient-api.yaml

* Modify `kube-configs/secrets.yaml` to use your configuration and the `channel.tx` generated

```
$ cat configuration/config.json | base64
$ cat configuration/channel.tx | base64

and place them in kube-configs/secrets.yaml

...
data:
  config.json: '<configruation/config.json in base64>'
  channel.tx: '<configruation/channel.tx in base64>'
...
```

### 4. Deploy the Blockchain Network in Kubernetes

* Change directory

```
cd kube-configs
```

* Create persistent volumes for the Blockchain network (state database, certificates, and ledger). This creates persistent volume claims and gets provisioned dynamically.

```
$ kubectl create -f persistent-volume

# wait for them to get provisioned using kubectl get

$ kubectl get pv,pvc

# you should see something like this:
#
# NAME                                          CAPACITY   ACCESSMODES   # RECLAIMPOLICY   STATUS    CLAIM                   STORAGECLASS     REASON    AGE
# pv/pvc-f57460fc-500f-11e8-a7d6-36f53fdc1872   20Gi       RWX           Delete          Bound     default/peer-claim      ibmc-file-gold             5d
# pv/pvc-f585de1d-500f-11e8-a7d6-36f53fdc1872   20Gi       RWX           Delete          Bound     default/org-ca-claim    ibmc-file-gold             5d
# pv/pvc-f5970726-500f-11e8-a7d6-36f53fdc1872   20Gi       RWX           Delete          Bound     default/couchdb-claim   ibmc-file-gold             5d
#
# NAME                STATUS    VOLUME                                     CAPACITY   ACCESSMODES   STORAGECLASS     AGE
# pvc/couchdb-claim   Bound     pvc-f5970726-500f-11e8-a7d6-36f53fdc1872   20Gi       RWX           ibmc-file-gold   5d
# pvc/org-ca-claim    Bound     pvc-f585de1d-500f-11e8-a7d6-36f53fdc1872   20Gi       RWX           ibmc-file-gold   5d
# pvc/peer-claim      Bound     pvc-f57460fc-500f-11e8-a7d6-36f53fdc1872   20Gi       RWX           ibmc-file-gold   5d
```

* Create the secrets and deploy the certificate authorities

```
$ kubectl create -f secrets.yaml
$ kubectl apply -f shop-ca.yaml
$ kubectl apply -f fitcoin-ca.yaml

# Wait for them to run

$ kubectl get pods
```

* Create couchdb. This is the statedb of the peers.

```
$ kubectl apply -f ca-datastore.yaml
$ kubectl apply -f fitcoin-statedb.yaml
$ kubectl apply -f shop-statedb.yaml

# Wait for them to run

$ kubectl get pods
```

* Create orderer and peers of the blockchain network. We have two organizations in the blockchain network, the Shop and Fitcoin.

```
$ kubectl apply -f orderer0.yaml
$ kubectl apply -f shop-peer.yaml
$ kubectl apply -f fitcoin-peer.yaml

# Wait for them to run

$ kubectl get pods
```

* Setup the Blockchain network with the blockchain-setup job. This creates and joins the peers in a channel and installs and instantiates the chaincode

```
$ kubectl apply -f blockchain-setup.yaml
```

* Check if the blockchain setup is complete by checking the logs of the blockchain-setup pod.

```
$ kubectl logs -l app=blockchain-setup

# should result into:
#
# Default channel not found, attempting creation...
# Successfully created a new default channel.
# Joining peers to the default channel.
# Chaincode is not installed, attempting installation...
# Base container image present.
# info: [packager/Golang.js]: packaging GOLANG from bcfit
# info: [packager/Golang.js]: packaging GOLANG from bcfit
# Successfully installed chaincode on the default channel.
# Successfully instantiated chaincode on all peers.
# Blockchain newtork setup complete.
```

* Once it is done, create the execution requests backend and the API client for the Blockchain network.

```
$ kubectl apply -f shop-backend.yaml
$ kubectl apply -f fitcoin-backend.yaml
$ kubectl apply -f rabbitclient-api.yaml

# Wait for them to run

$ kubectl get pods
```

* Test the blockchain network using its API client.

```
$ kubectl get svc rabbitclient-api

# NAME               CLUSTER-IP      EXTERNAL-IP     PORT(S)          AGE
# rabbitclient-api   172.21.40.201   169.61.17.000   3000:30726/TCP   14m
```

* Use the external IP of the service to do a curl request

```
$ export URL="http://169.61.17.000:3000"

$ curl -H "Content-Type: application/json" -X POST -d '{"type":"enroll","queue":"user_queue","params":{}}' "$URL/api/execute"

# you should get something like this
# {"status":"success","resultId":"7f90764a-8660-45f2-904d-47d8fb87a900"}
```

* Check the result using the `resultId` you got above. You should get a user ID.

```
$ curl $URL/api/results/RESULT_ID

# RESULT_ID from the previous step is 7f90764a-8660-45f2-904d-47d8fb87a900
# you should get something like this
# {"status":"done","result":"{\"message\":\"success\",\"result\":{\"user\":\"50f97085-376d-40b3-8992-a9a2e6d18668\",\"txId\":\"2fcddeae9ddece5c818fed626601fba80711f3583944f8e53632972792954e09\"}}"}

# if you {"status":"pending"}
# try again or check if the resultId you copied is correct
```

* Well done! Your blockchain network is ready to be integrated with the Android app. Proceed to the next step to deploy more Microservices that are needed for the mobile app.

### 5. Configure and Deploy Microservices in Kubernetes

* Build and push the Docker images. These services are used by the Android app. The android app gets its leaderboards and random user names and avatars from these services.

```
# Go back to the root directory of the repository.

$ cd containers/

$ docker build -t $DOCKERHUB_USERNAME/leaderboard:latest leaderboard/
$ docker build -t $DOCKERHUB_USERNAME/mobile-assets:latest mobile-assets/
$ docker build -t $DOCKERHUB_USERNAME/registeree-api:latest registeree-api/

$ docker push $DOCKERHUB_USERNAME/leaderboard:latest
$ docker push $DOCKERHUB_USERNAME/mobile-assets:latest
$ docker push $DOCKERHUB_USERNAME/registeree-api:latest
```

* Configure the yaml files to use your MongoDB instance. Get the certificate and URL in your Compose for MongoDB dashboard.

![mongo credentials](docs/mongo-credentials.png)

![certificate](docs/mongo-certificate.png)

* Encode the certificate in base64. You can do it in the terminal:

```
$ echo -n "< Paste the certificate here>" | base64

# output would be like
# LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURlVENDQW1HZ0F3SUJBZ0lFV3V0Z...
```

* Paste the output in `mongo-cert-secret.yaml`

```
...
data:
  mongo.cert: 'LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURlVENDQW1HZ0F3SUJBZ0lFV3V0Z...'
...
```

* Add the MongoDB URL in `leaderboard-api.yaml`, `mobile-assets.yaml`, `registeree-api.yaml` in the environment variable of `MONGODB_URL`

```
...
env:
  - name: MONGODB_URL
    value: 'mongodb://admin:QWERTY@sl-us-south-1-po...'
...
```

* You would also want to update the images declared in the files `leaderboard-api.yaml`, `mobile-assets.yaml` and `registeree-api.yaml`.

```
...
  containers:
    - image: <the corresponding image name that you have built>
...
```

* Deploy the microservices in kubernetes

```
# apply the certificate as a kubernetes secret
$ kubectl apply -f mongo-cert-secret.yaml

# deploy the microservices
$ kubectl apply -f leaderboard-api.yaml
$ kubectl apply -f mobile-assets.yaml
$ kubectl apply -f registeree-api.yaml
```

* The 3 microservices above should now be running. You could check by doing `kubectl get pods`

### 6. Expose the backend with Kubernetes Ingress

* You would want to expose the backend you deployed so that the Android app can communicate with it. With Kubernetes Ingress, this would allow you to expose these microservices. You can use the provided Ingress Subdomain that came with the IBM Cloud Container Service.

```
$ bx cs cluster-get <Your cluster name here>

# You should look for these values
# ..
# Ingress Subdomain:	anthony-blockchain.us-south.containers.mybluemix.net
# Ingress Secret:		anthony-blockchain
# ..
```

* Modify `ingress-prod.yaml` to use the provided subdomain you have

```
...
spec:
  tls:
  - hosts:
    - <Your-ingress-SUBDOMAIN-here>
    secretName: <Your-ingress-SECRET-here>
  backend:
    serviceName: mobile-assets
    servicePort: 80
  rules:
  - host: <Your-ingress-SUBDOMAIN-here>
...
```

* Apply the Kubernetes Ingress resource

```
$ kubectl apply -f ingress-prod.yaml
```

### 7. Configure the Android app

* Open the `android` folder in the Android Studio IDE. The project is configured to do REST calls to the Kubernetes backend. You would need to change the `BACKEND_URL` variables to your own **Ingress Subdomain** in these Java files:
  * ContractListAdapter
  * LeaderboardsFragment
  * MainActivity
  * QuantitySelection
  * ShopFragment
  * TechFragment
  * UserFragment

<img src="docs/java-files.png" alt="alt text" width="50%" height="50%">

> The line would look something like:

```java
...
String BACKEND_URL = "https://anthony-blockchain.us-south.containers.mybluemix.net";
...
```

### 8. Test the Android app

* You could test the android app either in an [emulator](https://developer.android.com/studio/run/emulator) in Android Studio or on a [real device](https://developer.android.com/studio/run/device). To test the tracking of steps, you would need a real device and an [OAuth 2.0 Client ID](https://developers.google.com/fit/android/get-api-key). This authorizes your app to use the Google Fit API.

![](docs/android-screen.png)

* Once you open the app, you are automatically enrolled in the Blockchain network. If you go to the Shop view, you'll also notice that it doesn't have any Swags available for trade. You would need to register a seller and add some products.

```
$ export URL="https://<Your-ingress-subdomain>"
$ curl -H "Content-Type: application/json" -X POST -d '{"type":"enroll","queue":"seller_queue","params":{}}' "$URL/api/execute"

# you will get something in the format of
# {"status":"success","resultId":"dfb6ef43-4eb2-467d-8c05-93ed5d0b517f"}
# using the result ID you got above, do:

$ curl "$URL/api/results/dfb6ef43-4eb2-467d-8c05-93ed5d0b517f"

# You will get:
# { "status":"done",
#   "result": "{\"message\":\"success\",\"result\":{\"user\":\"351a9993-c204-4f75-a38c-ccaba49929bc\",\"txId\":\"15698a529b7c1bc98725cb61177c621bac9de03347567c405bd68e1ff96b120a\"}}"}

# The ID of the seller would be 351a9993-c204-4f75-a38c-ccaba49929bc
```

* And now, add some products using the seller ID

```
$ export SELLER_ID=<seller ID you got from step above>

$ curl -H "Content-Type: application/json" -X POST -d '{"type":"invoke","queue":"seller_queue","params":{"userId": "'"$SELLER_ID"'", "fcn":"createProduct","args":["'"$SELLER_ID"'","eye-sticker","Eye sticker","100","1"]}}' "$URL/api/execute"
$ curl -H "Content-Type: application/json" -X POST -d '{"type":"invoke","queue":"seller_queue","params":{"userId": "'"$SELLER_ID"'", "fcn":"createProduct","args":["'"$SELLER_ID"'","bee-sticker","Bee sticker","100","1"]}}' "$URL/api/execute"
$ curl -H "Content-Type: application/json" -X POST -d '{"type":"invoke","queue":"seller_queue","params":{"userId": "'"$SELLER_ID"'", "fcn":"createProduct","args":["'"$SELLER_ID"'","em-sticker","M sticker","100","1"]}}' "$URL/api/execute"
```

> Learn more about the chaincode functions for this project [here](containers/blockchain/ccfunctions.md)

* The app is configured to reward users with 1 Kubecoin for every 100 steps they take.

# Links

* [Hyperledger Fabric Node SDK](https://fabric-sdk-node.github.io/): Node SDK to interact with the Hyperledger Fabric network.
* [Google Fit](https://developers.google.com/fit): Give your app the capability to get and update fitness information.
* [iOS version](https://github.com/IBM/kubecoin): A native iOS version of the app ineracting with the Hyperledger Fabric network.

# Learn more

* **Hyperledger Fabric Tutorials** Want to learn more about Hyperledger Fabric? Check out some [tutorials](https://hyperledger-fabric.readthedocs.io/en/release-1.1/tutorials.html).
* **Kubernetes on IBM Cloud**: Deploy and manage your containers in [Kubernetes on IBM Cloud](https://www.ibm.com/cloud/container-service)

# License
[Apache 2.0](LICENSE)
