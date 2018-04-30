const amqp = require('amqplib/callback_api');
var url = require('url');

export class RabbitClient {
  constructor(config) {
    this._config = config;
    this._channel = null;
    this._connection = null;
    this._queue = null;
  }

  async configureClient(count) {
    var self = this;
    try {
      //console.log("connecting to rabbit server : " + this._config.rabbitmq);
      self._connection = await new Promise(function (resolve, reject) {
        amqp.connect(self._config.rabbitmq, { servername: url.parse(self._config.rabbitmq).hostname }, function (err, conn) {
          if(err) {
            console.log("Error in connecting rabbit queueserver");
            reject(err);
          } else {
            resolve(conn);
          }
        });
      });
      self._connection.on('error', function () {
        console.log('Connection error event on rabbit client');
        self.stop().then(function () {
          self._connection = null;
          return self.configureClient();
        }).then(function () {
          console.log('Rabbit Client reconfigured');
        }).catch(err => {
          //console.log(err);
          throw err;
        });
      });
      self._connection.on('close', function () {
        console.log('Connection closed event on rabbit client');
        self._connection = null;
        return self.configureClient().then(function () {
          console.log('Rabbit Client reconfigured');
        }).catch(err => {
          //console.log(err);
          throw err;
        });
      });
      self._channel = await new Promise(function (resolve, reject) {
        self._connection.createChannel(function (err, ch) {
          if(err) {
            console.log("Error in creating rabbit channel");
            reject(err);
          } else {
            resolve(ch);
          }
        });
      });
      self._queue = await new Promise(function (resolve, reject) {
        self._channel.assertQueue('', {
          exclusive: true
        }, function (err, q) {
          if(err) {
            console.log("Error in creating queue");
            reject(err);
          } else {
            resolve(q);
          }
        });
      });
      count = 0;
    } catch(err) {
      count++;
      if(count < 10) {
        console.error("Error in channel setup " + err.message);
        console.error("Now attempting reconnect ...");
        await self.configureClient(count);
      } else {
        console.error(err);
        process.exit(-1);
      }
    }
  }
  async stop() {
    //console.log('stop');
    try {
      if(this._channel) {
        return await this._channel.close();
      } else {
        //console.warn('stopping but channel was not opened');
        return Promise.resolve();
      }
    } catch(e) {
      console.log("Error in closing channel");
    }
  }
}
