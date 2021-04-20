var assert = require('/lib/xp/testing.js');
var scheduler = require('/lib/xp/scheduler.js');

var resultExpected = {
    'name': 'myjob',
    'descriptor': 'appKey:task',
    'description': 'job description',
    'enabled': true,
    'config': {
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
    'creator': 'user:system:creator',
    'modifier': 'user:system:creator',
    'createdTime': '2016-11-02T10:36:00Z',
    'modifiedTime': '2016-11-02T10:36:00Z',
    'schedule': {
        'value': '2012-01-01T00:00:00Z',
        'type': 'ONE_TIME'
    }
};

function createJob() {
    scheduler.create({
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
}

exports.getJob = function () {
    createJob();

    var result = scheduler.get({
        name: 'myjob'

    });

    assert.assertJsonEquals(resultExpected, result);
};

exports.getNotExist = function () {

    var result = scheduler.get({
        name: 'myjob'
    });

    assert.assertNull(result);
};

exports.getNull = function () {

    try {
        scheduler.get({
            name: null
        });
    } catch (e) {
        assert.assertJsonEquals('name cannot be null', e.message);
    }
};
