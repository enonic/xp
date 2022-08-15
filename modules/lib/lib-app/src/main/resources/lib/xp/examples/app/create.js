var appLib = require('/lib/xp/app');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Create virtual app.
var result = appLib.createVirtualApplication({
    key: 'my-app',
});

log.info('Created app: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'my-app',
    displayName: 'app display name',
    vendorName: 'vendor name',
    vendorUrl: 'https://vendor.url',
    url: 'https://myapp.url',
    version: '1.0.0',
    minSystemVersion: '2.0.0',
    maxSystemVersion: '3.0.0',
    modifiedTime: '2020-09-25T10:00:00Z',
    started: true,
    system: false
}, result);

