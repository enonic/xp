var appLib = require('/lib/xp/app');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Delete virtual app.
var result = appLib.deleteVirtualApplication({
    key: 'myapp',
});

if (result) {
    log.info('Deleted app: myapp');
} else {
    log.info('Failed to delete app: myapp');
}

// END


assert.assertEquals(true, result);

