var assert = require('/lib/xp/testing.js');
var schedulerLib1 = require('/lib/xp/scheduler');

schedulerLib1.create({
    name: 'myjob',
    descriptor: 'appKey:task',
    description: 'job description',
    user: 'user:system:user',
    enabled: true,
    config: {
        a: 1
    },
    schedule: {type: 'ONE_TIME', value: '2012-01-01T00:00:00.00Z'}
});

// modifies existing scheduled job
// BEGIN
var schedulerLib = require('/lib/xp/scheduler');

var result = schedulerLib.modify({
    name: 'myjob',
    editor: (edit) => {
        edit.descriptor = 'appKey:new-task';
        edit.description = 'new job description';
        edit.user = 'user:system:new-user';
        edit.enabled = false;
        edit.config = {
            a1: 3
        };
        edit.schedule = {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'};

        return edit;
    }
});
// END

var expected = {
    'name': 'myjob',
    'descriptor': 'appKey:new-task',
    'description': 'new job description',
    'enabled': false,
    'config': {
        'a1': 3
    },
    'user': 'user:system:new-user',
    'creator': 'user:system:creator',
    'modifier': 'user:system:modifier',
    'createdTime': '2016-11-02T10:36:00Z',
    'modifiedTime': '2021-02-25T10:44:33.170079900Z',
    'schedule': {
        'value': '* * * * *',
        'timeZone': 'GMT+05:30',
        'type': 'CRON'
    }
};

assert.assertJsonEquals(expected, result);
