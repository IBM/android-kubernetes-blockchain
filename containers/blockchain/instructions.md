## Instructions for setting the blockchainNetwork

### Open a new terminal and run the following command:
```bash
export FABRIC_CFG_PATH=$(pwd)
chmod +x cryptogen
chmod +x configtxgen
chmod +x ./rabbitCluster/cluster-entrypoint.sh
chmod +x generate-certs.sh
chmod +x generate-cfgtx.sh
chmod +x docker-images.sh
chmod +x build.sh
chmod +x clean.sh
./build.sh
```

### Add compactions for CouchDB instances

In browser, open the web UI for all the couchdb instances and add the compaction rule.

For eg:
* Open  ```http://<IPAddress>:<Port>/_utils```
* Now select the Configuration tab and click on the `Add Option` button.
* Provide the following values for the input fields:
```
Selection: compactions
Option: _default
Value: [{db_fragmentation, "30%"}, {view_fragmentation, "30%"}]
```
* Now using terminal, do the following curl request for all the DB's:
Format :
```
curl -H "Content-Type: application/json" -X POST http://<IPAddress>:<Port>/<DBName>/_compact
```

###  Check the logs

**Command**
```bash
docker logs blockchain-setup
```
**Output:**
```bash
Register CA fitcoin-org
CA registration complete  FabricCAServices : {hostname: fitcoin-ca, port: 7054}
Register CA shop-org
CA registration complete  FabricCAServices : {hostname: shop-ca, port: 7054}
info: [EventHub.js]: _connect - options {"grpc.ssl_target_name_override":"shop-peer","grpc.default_authority":"shop-peer"}
info: [EventHub.js]: _connect - options {"grpc.ssl_target_name_override":"fitcoin-peer","grpc.default_authority":"fitcoin-peer"}
Default channel not found, attempting creation...
Successfully created a new default channel.
Joining peers to the default channel.
Chaincode is not installed, attempting installation...
Base container image present.
info: [packager/Golang.js]: packaging GOLANG from bcfit
info: [packager/Golang.js]: packaging GOLANG from bcfit
Successfully installed chaincode on the default channel.
Successfully instantiated chaincode on all peers.
```

**Command**
```bash
docker ps
```
**Output:**
```bash
80de48d4f372        dev-fitcoin-peer-bcfit-1-7df93f2b75a05e9e7896ce92dcb057539e271722e43eba5ff9c75aae902fdcce   "chaincode -peer.add…"   3 hours ago         Up 3 hours                                                                dev-fitcoin-peer-bcfit-1
0bb401fe44b9        backend                                                                                     "node index.js"          3 hours ago         Up 3 hours                                                                fitcoin_fitcoin-backend_1
f873f201e99c        backend                                                                                     "node index.js"          3 hours ago         Up 3 hours                                                                fitcoin_shop-backend_1
c7fdf4341ee9        rabbit-client                                                                               "node index.js"          3 hours ago         Up 3 hours          0.0.0.0:3000->3000/tcp                                rabbit-client
499cf8d837ea        redis-server                                                                                "/docker-entrypoint.…"   3 hours ago         Up 3 hours          6379/tcp, 0.0.0.0:7000-7007->7000-7007/tcp            fitcoin_redis-server_1
febba02cc941        dev-shop-peer-bcfit-1-0e0d4e71de9ac7df4d0d20dfcf583e3e63227edda600fe338485053387e09c50      "chaincode -peer.add…"   3 hours ago         Up 3 hours                                                                dev-shop-peer-bcfit-1
03141f47f646        haproxy:1.7                                                                                 "/docker-entrypoint.…"   3 hours ago         Up 3 hours          0.0.0.0:5672->5672/tcp, 0.0.0.0:15672->15672/tcp      rabbitmq
5ada66d99be3        rabbitmq:3-management                                                                       "/usr/local/bin/clus…"   3 hours ago         Up 3 hours          4369/tcp, 5671-5672/tcp, 15671-15672/tcp, 25672/tcp   rabbitmq3
f51d8f5d7fc7        rabbitmq:3-management                                                                       "/usr/local/bin/clus…"   3 hours ago         Up 3 hours          4369/tcp, 5671-5672/tcp, 15671-15672/tcp, 25672/tcp   rabbitmq2
172cabad39b4        rabbitmq:3-management                                                                       "docker-entrypoint.s…"   3 hours ago         Up 3 hours          4369/tcp, 5671-5672/tcp, 15671-15672/tcp, 25672/tcp   rabbitmq1
5ffcf480fbb6        blockchain-setup                                                                            "node index.js"          3 hours ago         Up 3 hours          3000/tcp                                              blockchain-setup
f58e488b23da        fitcoin-peer                                                                                "peer node start"        3 hours ago         Up 3 hours          0.0.0.0:8051->7051/tcp, 0.0.0.0:8053->7053/tcp        fitcoin-peer
8e4facc6fd4a        shop-peer                                                                                   "peer node start"        3 hours ago         Up 3 hours          0.0.0.0:7051->7051/tcp, 0.0.0.0:7053->7053/tcp        shop-peer
4297f9e1f45c        hyperledger/fabric-couchdb:x86_64-1.0.2                                                     "tini -- /docker-ent…"   3 hours ago         Up 3 hours          4369/tcp, 9100/tcp, 0.0.0.0:6984->5984/tcp            couchdb1
e0fdc5312585        orderer-peer                                                                                "orderer"                3 hours ago         Up 3 hours          0.0.0.0:7050->7050/tcp                                orderer0
56c209a37bed        shop-ca                                                                                     "fabric-ca-server st…"   3 hours ago         Up 3 hours          0.0.0.0:7054->7054/tcp                                shop-ca
1db6a6ca3a6f        fitcoin-ca                                                                                  "fabric-ca-server st…"   3 hours ago         Up 3 hours          0.0.0.0:8054->7054/tcp                                fitcoin-ca
c90b289a10e6        hyperledger/fabric-couchdb:x86_64-1.0.2                                                     "tini -- /docker-ent…"   3 hours ago         Up 3 hours          4369/tcp, 9100/tcp, 0.0.0.0:5984->5984/tcp            couchdb0
```

**Command**
```bash
docker logs fitcoin_fitcoin-backend_1
```
**Output:**
```
Register CA fitcoin-org
CA registration complete  FabricCAServices : {hostname: fitcoin-ca, port: 7054}
Register CA fitcoin-org
CA registration complete  FabricCAServices : {hostname: fitcoin-ca, port: 7054}
info: [EventHub.js]: _connect - options {"grpc.ssl_target_name_override":"fitcoin-peer","grpc.default_authority":"fitcoin-peer","grpc.max_receive_message_length":-1,"grpc.max_send_message_length":-1}
info: [EventHub.js]: _connect - options {"grpc.ssl_target_name_override":"fitcoin-peer","grpc.default_authority":"fitcoin-peer","grpc.max_receive_message_length":-1,"grpc.max_send_message_length":-1}
connected to the server
creating server queue connection user_queue
 [x] Awaiting RPC requests
```

**Command**
```bash
docker logs fitcoin_shop-backend_1
```
**Output:**
```
Register CA shop-org
CA registration complete  FabricCAServices : {hostname: shop-ca, port: 7054}
Register CA shop-org
CA registration complete  FabricCAServices : {hostname: shop-ca, port: 7054}
info: [EventHub.js]: _connect - options {"grpc.ssl_target_name_override":"shop-peer","grpc.default_authority":"shop-peer","grpc.max_receive_message_length":-1,"grpc.max_send_message_length":-1}
info: [EventHub.js]: _connect - options {"grpc.ssl_target_name_override":"shop-peer","grpc.default_authority":"shop-peer","grpc.max_receive_message_length":-1,"grpc.max_send_message_length":-1}
Starting socker server
connected to the server
creating server queue connection seller_queue
 [x] Awaiting RPC requests
```

**Scale the fictoin-backend**

To scale the fitcoin-backend use the following command:
```bash
docker-compose -p "fitcoin" up -d --scale fitcoin-backend=<No of conatiners>
```

**To run the load test application**

Check the instructions from [start.md](https://github.com/IBM/secret-map-dashboard/blob/master/containers/blockchain/cliLoadTester/start.md)

**To view the Blockchain Events**

In a separate terminal navigate to testApplication folder and run the following command:
```
npm install
node index.js
```
Navigate to url to view the blockchain blocks: **http://localhost:8000/history.html**
Now navigate to url to perform operations on network : **http://localhost:8000/test.html**

**Sample  values for request**

**Invoke Operation**
```
type = invoke
userId = <userID> i.e. user1
fcn = generateFitcoins
args = <userID>,<Number as String> i.e. user1,"500"
```

**Query Operation**
```
type = query
userId = <userID> i.e. user1
fcn = getState
args = <userID> i.e. user1
```
