var appLib = require('/lib/xp/app');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Create virtual app.
var result = appLib.getApplicationMode({
    key: 'my-app',
});

log.info('Application mode: ' + result);

// END


assert.assertEquals('AUGMENTED', result);

