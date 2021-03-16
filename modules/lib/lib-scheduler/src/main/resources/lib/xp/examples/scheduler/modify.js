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

// modifies existing scheduled job
// BEGIN
var schedulerLib = require('/lib/xp/scheduler');

var result = schedulerLib.modify({
    name: 'myjob',
    editor: (edit) => {
        edit.descriptor = 'appKey:new-task';
        edit.description = 'new job description';
        edit.user = 'user:system:new-user';
        edit.author = 'user:system:new-author';
        edit.enabled = false;
        edit.payload = {
            a1: 3
        };
        edit.calendar = {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'};

        return edit;
    }
});
// END

var expected = {
    'name': 'myjob',
    'descriptor': 'appKey:new-task',
    'description': 'new job description',
    'enabled': false,
    'payload': {
        'a1': 3
    },
    'user': 'user:system:new-user',
    'author': 'user:system:new-author',
    'calendar': {
        'value': '* * * * *',
        'timeZone': 'GMT+05:30',
        'type': 'CRON'
    }
};

assert.assertJsonEquals(expected, result);
