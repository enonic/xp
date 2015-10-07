var assert = Java.type('org.junit.Assert');

var result = require('./level1').test();
assert.assertEquals('OK', result);