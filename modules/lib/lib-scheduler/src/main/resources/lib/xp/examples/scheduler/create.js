var assert = require('/lib/xp/testing.js');

// Creates a scheduled one-time job with minimal properties
// BEGIN
var schedulerLib1 = require('/lib/xp/scheduler');

var simpleOneTimeJob = schedulerLib1.create({
    name: 'my-project',
    descriptor: 'appKey:task',
    enabled: true,
    calendar: {type: 'ONE_TIME', value: '2012-01-01T00:00:00.00Z'}
});
// END

var expectedOneTimeJob = {
    'name': 'my-project',
    'descriptor': 'appKey:task',
    'enabled': true,
    'payload': {},
    'calendar': {
        'value': '2012-01-01T00:00:00Z',
        'type': 'ONE_TIME'
    }
};

assert.assertJsonEquals(expectedOneTimeJob, simpleOneTimeJob);

// Creates a scheduled one-time job with extended properties
// BEGIN
var schedulerLib2 = require('/lib/xp/scheduler');

var extendedOneTimeJob = schedulerLib2.create({
    name: 'myjob',
    descriptor: 'appKey:task',
    description: 'job description',
    user: 'user:system:user',
    author: 'user:system:author',
    enabled: true,
    payload: {
        a: 1,
        b: 2,
        c: ['1', '2'],
        d: {
            e: {
                f: 3.6,
                g: true
            }
        }
    },
    calendar: {type: 'ONE_TIME', value: '2012-01-01T00:00:00.00Z'}
});
// END

var expectedExtendedOneTimeJob = {
    'name': 'myjob',
    'descriptor': 'appKey:task',
    'description': 'job description',
    'enabled': true,
    'payload': {
        'a': 1,
        'b': 2,
        'c': {
            '0': '1',
            '1': '2'
        },
        'd': {
            'e': {
                'f': 3.6,
                'g': true
            }
        }
    },
    'user': 'user:system:user',
    'author': 'user:system:author',
    'calendar': {
        'value': '2012-01-01T00:00:00Z',
        'type': 'ONE_TIME'
    }
};

assert.assertJsonEquals(expectedExtendedOneTimeJob, extendedOneTimeJob);


// Creates a scheduled cron job with minimal properties
// BEGIN
var schedulerLib3 = require('/lib/xp/scheduler');

var simpleCronJob = schedulerLib3.create({
    name: 'myjob',
    descriptor: 'appKey:task',
    enabled: true,
    calendar: {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'}
});
// END

var expectedSimpleCronJob = {
    'name': 'myjob',
    'descriptor': 'appKey:task',
    'enabled': true,
    'payload': {},
    'calendar': {
        'value': '* * * * *',
        'timeZone': 'GMT+05:30',
        'type': 'CRON'
    }
};

assert.assertJsonEquals(expectedSimpleCronJob, simpleCronJob);

// Creates a scheduled cron job with extended properties
// BEGIN
var schedulerLib4 = require('/lib/xp/scheduler');

var extendedCronJob = schedulerLib4.create({
    name: 'myjob',
    descriptor: 'appKey:task',
    description: 'job description',
    user: 'user:system:user',
    author: 'user:system:author',
    enabled: true,
    payload: {
        a: 1,
        b: 2,
        c: ['1', '2'],
        d: {
            e: {
                f: 3.6,
                g: true
            }
        }
    },
    calendar: {type: 'CRON', value: '* * * * 5', timeZone: 'GMT-2:00'}
});
// END

var expectedExtendedCronJob = {
    'name': 'myjob',
    'descriptor': 'appKey:task',
    'description': 'job description',
    'enabled': true,
    'payload': {
        'a': 1,
        'b': 2,
        'c': {
            '0': '1',
            '1': '2'
        },
        'd': {
            'e': {
                'f': 3.6,
                'g': true
            }
        }
    },
    'user': 'user:system:user',
    'author': 'user:system:author',
    'calendar': {
        'value': '* * * * 5',
        'timeZone': 'GMT-02:00',
        'type': 'CRON'
    }
};

assert.assertJsonEquals(expectedExtendedCronJob, extendedCronJob);
