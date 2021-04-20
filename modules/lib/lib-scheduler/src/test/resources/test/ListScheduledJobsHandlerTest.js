var assert = require('/lib/xp/testing.js');
var scheduler = require('/lib/xp/scheduler.js');

var result1Expected = [
    {
        'name': 'job1',
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
    }
];

var result2Expected = [
    {
        'name': 'job2',
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
    }
];

function createJob(name, schedule) {
    scheduler.create({
        name,
        descriptor: 'appKey:task',
        description: 'job description',
        user: 'user:system:user',
        enabled: true,
        config: {
            a: 1

        },
        schedule
    });
}

exports.listJobs = function () {
    assert.assertJsonEquals([], scheduler.list());

    createJob('job1', {type: 'ONE_TIME', value: '2012-01-01T00:00:00.00Z'});
    assert.assertJsonEquals(result1Expected, scheduler.list());

    createJob('job2', {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'});
    assert.assertJsonEquals(result2Expected, scheduler.list());
};
