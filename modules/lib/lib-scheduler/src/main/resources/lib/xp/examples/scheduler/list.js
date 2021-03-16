var assert = require('/lib/xp/testing.js');
var schedulerLib1 = require('/lib/xp/scheduler');

schedulerLib1.create({
    name: 'myjob1',
    descriptor: 'appKey:task1',
    enabled: false,
    calendar: {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'}
});

schedulerLib1.create({
    name: 'myjob2',
    descriptor: 'appKey:task2',
    description: 'job description',
    user: 'user:system:user',
    author: 'user:system:author',
    enabled: true,
    payload: {
        a: 1
    },
    calendar: {type: 'ONE_TIME', value: '2012-01-01T00:00:00.00Z'}
});

// Fetch all existing scheduled jobs
// BEGIN
var schedulerLib = require('/lib/xp/scheduler');

var result = schedulerLib.list();
// END

var expected = [
    {
        'name': 'myjob2',
        'descriptor': 'appKey:task2',
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
    },
    {
        'name': 'myjob1',
        'descriptor': 'appKey:task1',
        'enabled': false,
        'payload': {},
        'calendar': {
            'value': '* * * * *',
            'timeZone': 'GMT+05:30',
            'type': 'CRON'
        }
    }
];

assert.assertJsonEquals(expected, result);
