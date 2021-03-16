var assert = require('/lib/xp/testing.js');
var scheduler = require('/lib/xp/scheduler.js');

var createOneTimeJobExpected = {
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

exports.createOneTimeJob = function () {
    var result = scheduler.create({
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

    assert.assertJsonEquals(createOneTimeJobExpected, result);
};

var createCronJobExpected = {
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
        'value': '* * * * *',
        'timeZone': 'GMT+05:30',
        'type': 'CRON'
    }
};

exports.createCronJob = function () {
    var result = scheduler.create({
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
        calendar: {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'}
    });

    assert.assertJsonEquals(createCronJobExpected, result);
};

exports.createWithoutName = function () {

    try {

        scheduler.create({
            descriptor: 'appKey:task',
            enabled: true,
            payload: {},
            calendar: {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'}
        });
    } catch (e) {
        assert.assertEquals('Parameter \'name\' is required', e);
    }
};

exports.createWithoutCalendar = function () {

    try {

        scheduler.create({
            name: 'myjob',
            descriptor: 'appKey:task',
            enabled: true,
            payload: {}
        });
    } catch (e) {
        assert.assertEquals('Parameter \'calendar\' is required', e);
    }
};

exports.createWithoutDescriptor = function () {

    try {

        scheduler.create({
            name: 'myjob',
            enabled: true,
            payload: {},
            calendar: {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'}
        });
    } catch (e) {
        assert.assertEquals('Parameter \'descriptor\' is required', e);
    }
};

var withoutUserExpected = {
    'name': 'myjob',
    'descriptor': 'appKey:task',
    'description': 'job description',
    'enabled': true,
    'payload': {
        'a': 1
    },
    'author': 'user:system:author',
    'calendar': {
        'value': '* * * * *',
        'timeZone': 'GMT+05:30',
        'type': 'CRON'
    }
};

exports.createWithoutUser = function () {

    var result = scheduler.create({
        name: 'myjob',
        descriptor: 'appKey:task',
        description: 'job description',
        author: 'user:system:author',
        enabled: true,
        payload: {
            a: 1
        },
        calendar: {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'}
    });
    assert.assertJsonEquals(withoutUserExpected, result);

};

var withoutPayloadExpected = {
    'name': 'myjob',
    'descriptor': 'appKey:task',
    'description': 'job description',
    'enabled': true,
    'payload': {},
    'user': 'user:system:user',
    'author': 'user:system:author',
    'calendar': {
        'value': '* * * * *',
        'timeZone': 'GMT+05:30',
        'type': 'CRON'
    }
};

exports.createWithoutPayload = function () {

    var result = scheduler.create({
        name: 'myjob',
        descriptor: 'appKey:task',
        description: 'job description',
        author: 'user:system:author',
        user: 'user:system:user',
        enabled: true,
        calendar: {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'}
    });
    assert.assertJsonEquals(withoutPayloadExpected, result);

};

