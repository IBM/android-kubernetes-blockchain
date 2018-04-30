'use strict';
import config from '../set-up/config';
import {
  OrganizationClient
} from '../set-up/client';
import queryFunc from '../set-up/query';
const objArray = config.peers.map(obj => new OrganizationClient(config.channelName, config.orderer, obj.peer, obj.ca, obj.admin));
(async () => {
  try {
    await Promise.all(objArray.map(obj => obj.login()));
  } catch(e) {
    console.log('Fatal error logging into blockchain organization clients!');
    console.log(e);
    process.exit(-1);
  }
  await Promise.all(objArray.map(obj => obj.initEventHubs()));
  objArray.forEach(function(entry) {
    queryFunc("admin", entry, config.chaincodeId, config.chaincodeVersion, "query", ["a"]).then((result) => {
      console.log("Query Results for a : " + result);
    }).catch(err => {
      console.log("Error in Query for b : " + err);
    });
    queryFunc("admin", entry, config.chaincodeId, config.chaincodeVersion, "query", ["b"]).then((result) => {
      console.log("Query Results for b : " + result);
    }).catch(err => {
      console.log("Error in Query for b : " + err);
    });
  });
})();