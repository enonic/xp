var assert = require('/lib/xp/testing.js');
var scheduler = require('/lib/xp/scheduler.js');

var resultExpected = {
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

exports.modifyJob = function () {
    createJob();

    var result = scheduler.modify({
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

    assert.assertJsonEquals(resultExpected, result);
};

var modifyJobWithNullResultExpected = {
    'name': 'myjob',
    'descriptor': 'appKey:task',
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
    'creator': 'user:system:creator',
    'modifier': 'user:system:modifier',
    'createdTime': '2016-11-02T10:36:00Z',
    'modifiedTime': '2021-02-25T10:44:33.170079900Z',
    'schedule': {
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

exports.modifyConfigWithNull = function () {
    createJob();

    try {
        var result = scheduler.modify({
            name: 'myjob',
            editor: (edit) => {
                edit.config = null;

                return edit;
            }
        });
    } catch (e) {
        assert.assertJsonEquals('config cannot be null', e.message);
    }

};

exports.modifyCalendarWithNull = function () {
    createJob();

    try {
        var result = scheduler.modify({
            name: 'myjob',
            editor: (edit) => {
                edit.schedule = null;

                return edit;
            }
        });
    } catch (e) {
        assert.assertJsonEquals('schedule cannot be null', e.message);
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
