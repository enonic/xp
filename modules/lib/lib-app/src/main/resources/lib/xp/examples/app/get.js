var appLib = require('/lib/xp/app');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Create virtual app.
var result = appLib.get({
    key: 'my-app',
});

log.info('Fetched app: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'my-app',
    displayName: 'app display name',
    vendorName: 'vendor name',
    vendorUrl: 'https://vendor.url',
    url: 'https://myapp.url',
    version: '1.0.0',
    systemVersion: '4.2.3-SNAPSHOT',
    minSystemVersion: '2.0.0',
    maxSystemVersion: '3.0.0',
    modifiedTime: '2020-09-25T10:00:00Z',
    started: true,
    system: true
}, result);

