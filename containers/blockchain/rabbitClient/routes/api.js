const express = require("express");
const router = express.Router();
const uuidv4 = require('uuid/v4');
const utils = require('../utils/util');
router.post('/execute', function (req, res) {
  var resultId = uuidv4();
  if(!req.body.queue) {
    res.json({
      status: "failed",
      resultId: "Invalid Request. Missing queue name"
    });
  } else {
    utils.queueRequest(resultId, req.body.queue, req.body);
    res.json({
      status: "success",
      resultId: resultId
    });
  }
});
router.get('/results/:resultId', function (req, res, next) {
  var redisClient = utils.getRedisConnection();
  redisClient.get(req.params.resultId, function (error, result) {
    if(error) {
      next(error);
    }
    if(!result) {
      res.json({
        status: "pending"
      });
    } else {
      res.json({
        status: "done",
        result: result
      });
    }
    redisClient.quit();
  });
});
exports.router = router;
