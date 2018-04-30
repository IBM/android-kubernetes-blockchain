const express = require("express");
const app = express();
const mongoose = require("mongoose");
const assert = require("assert");
const fs = require("fs");
const cors = require("cors");

const leaderboardRoute = require("./routes/leaderboard");

const mongoDbUrl = process.env.MONGODB_URL;
let ca;
if (process.env.MONGODB_CERT_BASE64) { // if encoded certificate is set in ENV, use it.
  ca = new Buffer(process.env.MONGODB_CERT_BASE64, "base64");
} else if (fs.existsSync("/etc/ssl/mongo.cert")) { // if mongo.cert exists, use it
  ca = [fs.readFileSync("/etc/ssl/mongo.cert")];
} else if (process.env.UNIT_TEST == "test") { // if a test, don't do anything.
  console.log("This is a test. Run a local mongoDB.");
} else {
  console.log("No certificate provided!");
}

let mongoDbOptions = {
  mongos: {
    useMongoClient: true,
    ssl: true,
    sslValidate: true,
    sslCA: ca,
  },
};

mongoose.connection.on("error", function(err) {
  console.log("Mongoose default connection error: " + err);
});

mongoose.connection.on("open", function(err) {
  console.log("CONNECTED...");
  assert.equal(null, err);
});

if (process.env.UNIT_TEST == "test") {
  mongoDbOptions = {
    useMongoClient: true,
  };
  mongoose.connect("mongodb://localhost/test", mongoDbOptions);
}
else {
  mongoose.connect(mongoDbUrl, mongoDbOptions);
}

app.use(require("body-parser").json());
app.use(cors());

app.use(express.static(__dirname + "/public"));

app.use("/leaderboard", leaderboardRoute);

let port = process.env.PORT || 8080;
app.listen(port, function() {
  console.log("To view your app, open this link in your browser: http://localhost:" + port);
});

module.exports = app;
