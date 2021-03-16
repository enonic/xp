var assert = require('/lib/xp/testing.js');
var scheduler = require('/lib/xp/scheduler.js');

var resultExpected = {
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


exports.modifyJob = function () {
    createJob();

    var result = scheduler.modify({
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

    assert.assertJsonEquals(resultExpected, result);
};

var modifyJobWithNullResultExpected = {
    'name': 'myjob',
    'descriptor': 'appKey:task',
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
    'calendar': {
        'value': '2012-01-01T00:00:00Z',
        'type': 'ONE_TIME'
    }
};

exports.modifyJobWithNull = function () {
    createJob();

    var result = scheduler.modify({
        name: 'myjob',
        editor: (edit) => {
            edit.description = null;
            edit.user = null;
            edit.author = null;

            return edit;
        }
    });

    assert.assertJsonEquals(modifyJobWithNullResultExpected, result);
};

exports.modifyDescriptorWithNull = function () {
    createJob();

    try {
        var result = scheduler.modify({
            name: 'myjob',
            editor: (edit) => {
                edit.descriptor = null;

                return edit;
            }
        });
    } catch (e) {
        assert.assertJsonEquals('descriptor cannot be null', e.message);
    }

};

exports.modifyPayloadWithNull = function () {
    createJob();

    try {
        var result = scheduler.modify({
            name: 'myjob',
            editor: (edit) => {
                edit.payload = null;

                return edit;
            }
        });
    } catch (e) {
        assert.assertJsonEquals('payload cannot be null', e.message);
    }

};

exports.modifyCalendarWithNull = function () {
    createJob();

    try {
        var result = scheduler.modify({
            name: 'myjob',
            editor: (edit) => {
                edit.calendar = null;

                return edit;
            }
        });
    } catch (e) {
        assert.assertJsonEquals('calendar cannot be null', e.message);
    }
};

exports.modifyEnabledWithNull = function () {
    createJob();

    try {
        var result = scheduler.modify({
            name: 'myjob',
            editor: (edit) => {
                edit.enabled = null;

                return edit;
            }
        });
    } catch (e) {
        assert.assertJsonEquals('enabled cannot be null', e.message);
    }
};

function createJob() {
    scheduler.create({
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
}
