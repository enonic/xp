var appLib = require('/lib/xp/app');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Create virtual app.
var result = appLib.hasReal({
    key: 'my-app',
});

log.info('Has real: ' + result);

// END


assert.assertEquals(true, result);

