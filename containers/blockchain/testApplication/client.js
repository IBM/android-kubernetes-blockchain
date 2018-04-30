var amqp = require('amqplib/callback_api');
var start = Date.now();

function requestServer(params, reqQueue) {
  amqp.connect('amqp://localhost:5672', function (err, conn) {
    conn.createChannel(function (err, ch) {
      ch.assertQueue('', {
        exclusive: true
      }, function (err, q) {
        var corr = generateUuid();
        params = JSON.stringify(params);
        console.log(' [x] Requesting %s', params);
        ch.consume(q.queue, function (msg) {
          if(msg.properties.correlationId === corr) {
            msg.content = JSON.parse(msg.content.toString());
            if(msg.content.message === "success") {
              console.log(' [.] Query Result ');
              console.log(msg.content);
              //conn.close();
              var millis = Date.now() - start;
              console.log("seconds elapsed = " + Math.floor(millis / 1000));
              setTimeout(function () {
                conn.close();
              }, 500);
            } else if(msg.content.message === "failed" && msg.content.error.includes('READ_CONFLICT') && parseInt(msg.properties.messageId) < 3) {
              console.log("Error in query. Request Attempt No : " + (parseInt(msg.properties.messageId) + 1));
              //  console.log("Error in query. Queueing request");
              console.log(' [x] Requesting %s', params);
              setTimeout(() => {
                ch.sendToQueue(reqQueue, new Buffer(params), {
                  correlationId: msg.properties.correlationId,
                  messageId: (parseInt(msg.properties.messageId) + 1).toString(),
                  replyTo: q.queue
                });
              }, 20000);
            } else {
              console.log(' [.] Query Result ');
              console.log(msg.content);
              conn.close();
            }
          }
        }, {
          noAck: true
        });
        ch.sendToQueue(reqQueue, new Buffer(params), {
          correlationId: corr,
          messageId: "1",
          replyTo: q.queue
        });
      });
    });
  });
}

function generateUuid() {
  return Math.random().toString() + Math.random().toString() + Math.random().toString();
}
//var sellerIds = ["f06939d3-9370-4019-9bb2-f4fba4c785ba", "468d09c4-d225-4007-9bf9-4fd78ef4835f", "4fe0a3af-0700-4d45-8fbf-4c86df783539", "22699066-91a6-45e0-81ce-fcf5d1063992", "ddc0d0a7-0384-4c4d-aa3d-da3e6bd3a2eb", "eb649140-a936-41fe-803f-ab1313efe300", "6f904e4f-5dc3-435e-8189-c91250b95ce9", "e2d1b74b-9158-497d-af21-06ce7f2cd283", "e823a8ee-d673-4798-a3b1-eb691b84e206"];
var ids = ["8b280ce1-6717-43e3-b8b2-adf85b0bbf96", "c1eb016c-d9a3-4bc6-a530-58165b0de8aa", "151f5ca5-be48-4f5c-a72a-ec94cb34396d", "3437d57f-7bb4-4868-8947-5a37fe25b728", "ee579e0e-8d00-4cfd-9366-ba8c418f29a2", "64c22a10-eea2-4f97-9e6c-e399a29a5bbf", "144bb6da-302b-45ea-a157-d3c7329bba73", "302b69b9-2197-4a0e-b27c-03c8d87066c4", "d1e8512e-41ad-4de3-bd6a-b6e6e5bc98a5", "4989a254-5f51-44ce-a5dd-19a4a7afea13"];
var base = 1000;
var queue = 'user_queue';

function generateCoins(ids, inc, queue) {
  for(var i = 0; i < ids.length; i++) {
    requestServer({
      type: "invoke",
      params: {
        "userId": ids[i],
        "fcn": "generateFitcoins",
        "args": [ids[i], (base + inc).toString()]
      }
    }, queue);
  }
}

function getValues(ids, queue) {
  for(var i = 0; i < ids.length; i++) {
    requestServer({
      type: "query",
      params: {
        "userId": ids[i],
        "fcn": "getState",
        "args": [ids[i]]
      }
    }, queue);
  }
}
/*
type:invoke
params:{"userId" : "c468865f-586d-4b28-8075-cccd1f43a720" , "fcn" : "generateFitcoins" , "args" : ["c468865f-586d-4b28-8075-cccd1f43a720","1000"]}

*/
getValues(ids, queue);
generateCoins(ids, 4000, queue);
getValues(ids, queue);
generateCoins(ids, 5000, queue);
getValues(ids, queue);
generateCoins(ids, 6000, queue);
getValues(ids, queue);