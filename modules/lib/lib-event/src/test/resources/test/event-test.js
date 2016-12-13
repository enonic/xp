var assert = require('/lib/xp/assert');
var eventLib = require('/lib/xp/event');

exports.testListener = function () {
    var event = {};
    eventLib.listener({
        type: 'app*',
        localOnly: false,
        callback: function (e) {
            event = e;
        }
    });

    testInstance.fireEvent();

    event.timestamp = 1;
    assert.assertJsonEquals(
        {
            "type": "application",
            "timestamp": 1,
            "localOrigin": true,
            "distributed": false,
            "data": {
                "a": 1
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

    var event = testInstance.publishedEvent;
    assert.assertEquals('custom.myEvent', event.type);
    assert.assertEquals(true, event.distributed);
    assert.assertEquals('{a=1, b=2}', event.data.toString());
};
