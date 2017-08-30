var eventLib = require('/lib/xp/event');
var assert = require('/lib/xp/testing');

// BEGIN
// Sends a custom event named "custom.myEvent".
eventLib.send({
    type: 'myEvent',
    distributed: false,
    data: {
        a: 1,
        b: 2
    }
});
// END
