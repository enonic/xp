var assert = require('/lib/xp/testing');
var eventLib = require('/lib/xp/event');

exports.testListener = function () {
    var event = {};
    eventLib.listener({
        type: 'app*',
        localOnly: false,
        callback(e) {
            event = e;
        }
    });

    testInstance.fireEvent();

    event.timestamp = 1;
    assert.assertJsonEquals(
        {
            'type': 'application',
            'timestamp': 1,
            'localOrigin': true,
            'distributed': false,
            'data': {
                'a': 1
            }
        }, event);
};

exports.testSend = function () {
    eventLib.send({
        type: 'myEvent',
        distributed: true,
        data: {
            a: 1,
            b: 2
        }
    });

    testInstance.checkPublishedEvent();
};
