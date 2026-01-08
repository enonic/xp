const assert = require('/lib/xp/testing.js');
const schedulerLib = require('/lib/xp/scheduler');

schedulerLib.create({
    name: 'myJob',
    descriptor: 'appKey:task',
    enabled: false,
    schedule: {type: 'ONE_TIME', value: '2012-01-01T00:00:00.00Z'}
});

// Deletes existing scheduled job
// BEGIN
var result = schedulerLib.deleteJob({
    name: 'myJob'
});
// END

assert.assertTrue(result);
