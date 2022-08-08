var appLib = require('/lib/xp/app');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Create virtual app.
var result = appLib.hasVirtual({
    key: 'my-app',
});

log.info('Has virtual: ' + result);

// END


assert.assertEquals(true, result);

