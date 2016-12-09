var eventLib = require('/lib/xp/event');
var assert = require('/lib/xp/assert');

// BEGIN
// Adds an event listener on all node events.
eventLib.listener('node.*', function (event) {
    log.info(JSON.stringify(event));
});
// END
