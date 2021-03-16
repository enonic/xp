var assert = require('/lib/xp/testing.js');
var schedulerLib1 = require('/lib/xp/scheduler');

schedulerLib1.create({
    name: 'myjob',
    descriptor: 'appKey:task',
    description: 'job description',
    user: 'user:system:user',
    author: 'user:system:author',
    enabled: true,
    payload: {
        a: 1
    },
    calendar: {type: 'ONE_TIME', value: '2012-01-01T00:00:00.00Z'}
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
    'payload': {
        'a': 1
    },
    'user': 'user:system:user',
    'author': 'user:system:author',
    'calendar': {
        'value': '2012-01-01T00:00:00Z',
        'type': 'ONE_TIME'
    }
};

assert.assertJsonEquals(expected, result);
