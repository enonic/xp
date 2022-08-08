var appLib = require('/lib/xp/app');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Create virtual app.
var result = appLib.getDescriptor({
    key: 'my-app',
});

log.info('Fetched app descriptor: ' + result.key);

// END


assert.assertJsonEquals({
    key: 'my-app',
    description: 'my app description',
    icon: {
        data: {},
        mimeType: 'image/png',
        modifiedTime: '2021-12-03T10:15:30Z'
    }
}, result);

