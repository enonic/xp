var eventLib = require('/lib/xp/event');

// BEGIN
// Sends a custom event named "custom.myEvent".
eventLib.send({
    type: 'myEvent',
    distributed: false,
    data: {
        a: 1,
        b: 2,
    },
});
// END
