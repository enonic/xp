var assert = require('/lib/xp/testing.js');
var scheduler = require('/lib/xp/scheduler.js');

var result1Expected = [
    {
        'name': 'job1',
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
    }
];

var result2Expected = [
    {
        'name': 'job2',
        'descriptor': 'appKey:task',
        'description': 'job description',
        'enabled': true,
        'payload': {
            'a': 1
        },
        'user': 'user:system:user',
        'author': 'user:system:author',
        'calendar': {
            'value': '* * * * *',
            'timeZone': 'GMT+05:30',
            'type': 'CRON'
        }
    },
    {
        'name': 'job1',
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
    }
];

function createJob(name, calendar) {
    scheduler.create({
        name,
        descriptor: 'appKey:task',
        description: 'job description',
        user: 'user:system:user',
        author: 'user:system:author',
        enabled: true,
        payload: {
            a: 1

        },
        calendar
    });
}

exports.listJobs = function () {
    assert.assertJsonEquals([], scheduler.list());

    createJob('job1', {type: 'ONE_TIME', value: '2012-01-01T00:00:00.00Z'});
    assert.assertJsonEquals(result1Expected, scheduler.list());

    createJob('job2', {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'});
    assert.assertJsonEquals(result2Expected, scheduler.list());
};
