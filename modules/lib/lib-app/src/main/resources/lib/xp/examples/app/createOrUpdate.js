var appLib = require('/lib/xp/app');
var assert = require('/lib/xp/testing');

/* global log, testInstance */

// BEGIN
// Create or update the application descriptor and icon: fields left untouched keep their value.
var result = appLib.createOrUpdate({
    key: 'my_app',
    editor: function (descriptor) {
        descriptor.title = 'My App';
        descriptor.description = 'Created from a script';
        descriptor.vendorName = 'Enonic';
        descriptor.config = {feature: true, limit: 42};
        descriptor.icon = {
            data: testInstance.createByteSource('<svg/>'),
            mimeType: 'image/svg+xml'
        };
        return descriptor;
    }
});

log.info('Application descriptor saved: ' + result.key);
// END

assert.assertJsonEquals({
    key: 'my_app',
    description: 'Created from a script',
    title: 'My App',
    vendorName: 'Enonic',
    config: {
        feature: true,
        limit: 42
    },
    icon: {
        data: {},
        mimeType: 'image/svg+xml',
        modifiedTime: '2026-10-08T10:00:00Z'
    }
}, result);
