var assert = require('/lib/xp/assert');
var eventLib = require('/lib/xp/event');

exports.testListener = function () {
    var event = {};
    eventLib.listener('app*', function (e) {
        event = e;
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
