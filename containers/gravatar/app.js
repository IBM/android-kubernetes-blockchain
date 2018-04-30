const express = require('express');
const minifig = require('minifig');
const svg2png = require('svg2png');
const random_name = require('node-random-name')
const app = express();

app.get('/gravatar/new', function(req, res) {

  minifig.makeSVG(function(svg) {
    var pngBuffer = svg2png.sync(new Buffer(svg),{width:300,height:300});

    res.send({name:random_name(),png:pngBuffer.toString('base64')})
  });

});

let port = process.env.PORT || 8080;
app.listen(port, function() {
  console.log("To view your app, open this link in your browser: http://localhost:" + port);
});
