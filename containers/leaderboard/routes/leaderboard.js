const express = require("express");
const router = express.Router();

const Registerees = require("../models/registeree");

// endpoints for registeree
router.get("/all", function(req, res) {
  Registerees.find({}, 'registereeId name steps -_id', {sort:{steps:-1}},function(err, registerees) {
    if (err) {
      res.send(err);
    }
    else {
      res.send(registerees);
    }
  });
});

router.get("/top/:number", function(req, res) {
  Registerees.find({}, 'registereeId name steps png -_id',{limit:parseInt(req.params.number),sort:{steps:-1}}, function(err, registerees) {
    if (err) {
      res.send(err);
    }
    else {
      res.send(registerees);
    }
  });
});

router.get("/position/steps/:userSteps", function(req, res) {
  Registerees.count({steps:{$gt:parseInt(req.params.userSteps)}}, function(err, position) {
    if (err) {
      res.send(err);
    }
    else {
      res.send({userPosition: position+1});
    }
  });
});

router.get("/position/user/:registereeId", function(req, res) {
  Registerees.findOne(req.params, function(err, registeree) {
    if (err){
      res.send(err);
    } else if (registeree) {
      Registerees.count({steps:{$gt:parseInt(registeree.steps)}}, function(err, position) {
        if (err) {
          res.send(err)
        } else {
          Registerees.count(function(err, count) {
            if (err) {
              res.send(err);
            }
            else {
              res.send({"userPosition":position+1, "count":count, "steps": registeree.steps});
            }
          });
        }
      })
    } else {
      res.send('Registeree not found...');
    }
  });
});

module.exports = router;
