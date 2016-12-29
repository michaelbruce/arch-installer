var cljs = require('clojurescript-nodejs');
var fs = require('fs');

var script = fs.readFileSync('install.cljs', 'utf8');

cljs.eval(script);
