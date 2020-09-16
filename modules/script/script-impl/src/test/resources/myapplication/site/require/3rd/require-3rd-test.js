var assert = Java.type('org.junit.jupiter.api.Assertions');

var moment = require('./moment.min.js');
assert.assertEquals(true, moment !== undefined);

var date = moment('12-25-1995', 'MM-DD-YYYY');
assert.assertEquals('1995', date.format('YYYY'));
