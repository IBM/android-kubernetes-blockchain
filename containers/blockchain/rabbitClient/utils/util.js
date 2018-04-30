import config from './config';
/*var config = {
  rabbitmq: 'amqp://localhost:5672',
  redisHost: 'localhost',
  redisPort: 7000,
};*/
import {
  RabbitClient
} from './channel';
// var RedisClustr = require('redis-clustr');
var Redis = require('ioredis');
const rabbitClient = new RabbitClient(config);
(async () => {
  try {
    await rabbitClient.configureClient(0);
    console.log("Rabbit client configured");
  } catch(e) {
    console.error(e);
    process.exit(-1);
  }
})();
export async function queueRequest(corrId, requestQueue, params) {
  params = typeof params !== "string" ? JSON.stringify(params) : params;
  console.log(' [x] Requesting %s', params);
  rabbitClient._channel.sendToQueue(requestQueue, new Buffer(params), {
    persistent: true,
    correlationId: corrId
  });
}
export function getRedisConnection() {
  return new Redis(config.redisUrl);
}
