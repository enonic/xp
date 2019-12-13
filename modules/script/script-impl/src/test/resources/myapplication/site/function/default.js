var assert = Java.type('org.junit.jupiter.api.Assertions');

var result = require('./level1').test();
assert.assertEquals('OK', result);
