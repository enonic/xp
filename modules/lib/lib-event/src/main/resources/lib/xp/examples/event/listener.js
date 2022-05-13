var eventLib = require('/lib/xp/event');

// BEGIN
// Adds an event listener on all node events.
eventLib.listener({
    type: 'node.*',
    localOnly: false,
    callback(event) {
        log.info(JSON.stringify(event));
    },
});
// END
