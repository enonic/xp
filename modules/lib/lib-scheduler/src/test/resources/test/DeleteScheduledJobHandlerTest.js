var assert = require('/lib/xp/testing.js');
var scheduler = require('/lib/xp/scheduler.js');

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

exports.deleteJob = function () {
    createJob();

    var result = scheduler.delete({
        name: 'myjob'
    });

    assert.assertJsonEquals(true, result);
};

exports.deleteNotExist = function () {

    var result = scheduler.delete({
        name: 'myjob'
    });

    assert.assertJsonEquals(false, result);
};

exports.deleteNull = function () {

    try {
        scheduler.delete({
            name: null
        });PortalRequestHelperTest
    } catch (e) {
        assert.assertEquals('name cannot be null', e.getMessage());
    }
};
