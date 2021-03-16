var assert = require('/lib/xp/testing.js');
var schedulerLib1 = require('/lib/xp/scheduler');

schedulerLib1.create({
    name: 'myJob',
    descriptor: 'appKey:task',
    enabled: false,
    calendar: {type: 'ONE_TIME', value: '2012-01-01T00:00:00.00Z'}
});

// Deletes existing scheduled job
// BEGIN
var schedulerLib = require('/lib/xp/scheduler');

var result = schedulerLib.delete({
    name: 'myJob'
});
// END

assert.assertTrue(result);
