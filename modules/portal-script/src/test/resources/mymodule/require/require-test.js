var other = require('other');
assert.assertEquals('Hello World!', other.hello('World'));

other = require('./other.js');
assert.assertEquals('Hello World!', other.hello('World'));

other = require('/require/other.js');
assert.assertEquals('Hello World!', other.hello('World'));

var util = require('util');
assert.assertEquals('Hello from Lib!', util.hello());
