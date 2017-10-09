var express = require("express");
var app     = express();
var path    = require("path");

app.use("/app-angular2/", express.static(__dirname + '/webapp')); 

app.listen(8080);
console.log("Running at Port 8080");
console.log('dirname is ' + __dirname);