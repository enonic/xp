var assert = require('/lib/xp/testing.js');
var schedulerLib1 = require('/lib/xp/scheduler');

schedulerLib1.create({
    name: 'myjob',
    descriptor: 'appKey:task',
    description: 'job description',
    user: 'user:system:user',
    creator: 'user:system:creator',
    modifier: 'user:system:modifier',
    enabled: true,
    config: {
        a: 1
    },
    schedule: {type: 'ONE_TIME', value: '2012-01-01T00:00:00.00Z'}
});

// Fetch existing scheduled job
// BEGIN
var schedulerLib = require('/lib/xp/scheduler');

var result = schedulerLib.get({
    name: 'myjob'
});
// END

var expected = {
    'name': 'myjob',
    'descriptor': 'appKey:task',
    'description': 'job description',
    'enabled': true,
    'config': {
        'a': 1
    },
    'user': 'user:system:user',
    'creator': 'user:system:creator',
    'modifier': 'user:system:creator',
    'createdTime': '2016-11-02T10:36:00Z',
    'modifiedTime': '2016-11-02T10:36:00Z',
    'schedule': {
        'value': '2012-01-01T00:00:00Z',
        'type': 'ONE_TIME'
    }
};

assert.assertJsonEquals(expected, result);
