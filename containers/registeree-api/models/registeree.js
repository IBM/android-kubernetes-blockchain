const mongoose = require("mongoose");

// eslint-disable-next-line
let registereeSchema = mongoose.Schema({
  registereeId: {type: String, unique: true},
  calories: Number,
  steps: {type: Number, required: true},
  name: String,
  png: String,
  device: String
});

module.exports = mongoose.model("Registeree", registereeSchema);
