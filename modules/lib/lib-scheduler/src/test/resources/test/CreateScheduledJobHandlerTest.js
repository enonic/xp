var assert = require('/lib/xp/testing.js');
var scheduler = require('/lib/xp/scheduler.js');

var createOneTimeJobExpected = {
    'name': 'myjob',
    'descriptor': 'appKey:task',
    'description': 'job description',
    'enabled': true,
    'config': {
        'a': 1,
        'b': 2,
        'c': ['1', '2'],
        'd': {
            'e': {
                'f': 3.6,
                'g': true
            }
        }
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

exports.createOneTimeJob = function () {
    var result = scheduler.create({
        name: 'myjob',
        descriptor: 'appKey:task',
        description: 'job description',
        user: 'user:system:user',
        enabled: true,
        config: {
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
        schedule: {type: 'ONE_TIME', value: '2012-01-01T00:00:00.00Z'}
    });

    assert.assertJsonEquals(createOneTimeJobExpected, result);
};

var createCronJobExpected = {
    'name': 'myjob',
    'descriptor': 'appKey:task',
    'description': 'job description',
    'enabled': true,
    'config': {
        'a': 1,
        'b': 2,
        'c': ['1', '2'],
        'd': {
            'e': {
                'f': 3.6,
                'g': true
            }
        }
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
};

exports.createCronJob = function () {
    var result = scheduler.create({
        name: 'myjob',
        descriptor: 'appKey:task',
        description: 'job description',
        user: 'user:system:user',
        enabled: true,
        config: {
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
        schedule: {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'}
    });

    assert.assertJsonEquals(createCronJobExpected, result);
};

exports.createWithoutName = function () {

    try {

        scheduler.create({
            descriptor: 'appKey:task',
            enabled: true,
            config: {},
            schedule: {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'}
        });
    } catch (e) {
        assert.assertEquals('Parameter \'name\' is required', e.message);
    }
};

exports.createWithoutCalendar = function () {

    try {

        scheduler.create({
            name: 'myjob',
            descriptor: 'appKey:task',
            enabled: true,
            config: {}
        });
    } catch (e) {
        assert.assertEquals('Parameter \'schedule\' is required', e.message);
    }
};

exports.createWithoutDescriptor = function () {

    try {

        scheduler.create({
            name: 'myjob',
            enabled: true,
            config: {},
            schedule: {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'}
        });
    } catch (e) {
        assert.assertEquals('Parameter \'descriptor\' is required', e.message);
    }
};

var withoutUserExpected = {
    'name': 'myjob',
    'descriptor': 'appKey:task',
    'description': 'job description',
    'enabled': true,
    'config': {
        'a': 1
    },
    'creator': 'user:system:creator',
    'modifier': 'user:system:creator',
    'createdTime': '2016-11-02T10:36:00Z',
    'modifiedTime': '2016-11-02T10:36:00Z',
    'schedule': {
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
        enabled: true,
        config: {
            a: 1
        },
        schedule: {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'}
    });
    assert.assertJsonEquals(withoutUserExpected, result);

};

var withoutConfigExpected = {
    'name': 'myjob',
    'descriptor': 'appKey:task',
    'description': 'job description',
    'enabled': true,
    'config': {},
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
};

exports.createWithoutConfig = function () {

    var result = scheduler.create({
        name: 'myjob',
        descriptor: 'appKey:task',
        description: 'job description',
        user: 'user:system:user',
        enabled: true,
        schedule: {type: 'CRON', value: '* * * * *', timeZone: 'GMT+5:30'}
    });
    assert.assertJsonEquals(withoutConfigExpected, result);

};

