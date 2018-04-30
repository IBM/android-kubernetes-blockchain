const mongoose = require("mongoose");

// eslint-disable-next-line
let pageSchema = mongoose.Schema({
  page: {type: Number, unique: true},
  title: String,
  subtitle: String,
  subtext: String,
  description: String,
  image: String,
  imageEncoded: String,
});

module.exports = mongoose.model("Page", pageSchema);
