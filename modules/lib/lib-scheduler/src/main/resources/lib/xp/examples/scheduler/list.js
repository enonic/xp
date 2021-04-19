var assert = require('/lib/xp/testing.js');
var schedulerLib1 = require('/lib/xp/scheduler');

schedulerLib1.create({
    name: 'myjob1',
    descriptor: 'appKey:task1',
    enabled: false,
    schedule: {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'}
});

schedulerLib1.create({
    name: 'myjob2',
    descriptor: 'appKey:task2',
    description: 'job description',
    user: 'user:system:user',
    enabled: true,
    config: {
        a: 1
    },
    schedule: {type: 'ONE_TIME', value: '2012-01-01T00:00:00.00Z'}
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
    },
    {
        'name': 'myjob1',
        'descriptor': 'appKey:task1',
        'enabled': false,
        'config': {},
        'creator': 'user:system:creator',
        'modifier': 'user:system:creator',
        'createdTime': '2016-11-02T10:36:00Z',
        'modifiedTime': '2016-11-02T10:36:00Z',
        'schedule': {
            'value': '* * * * *',
            'timeZone': 'GMT+05:30',
            'type': 'CRON'
        }
    }
];

assert.assertJsonEquals(expected, result);
