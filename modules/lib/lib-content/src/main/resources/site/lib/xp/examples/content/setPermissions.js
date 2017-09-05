var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Set permissions for content by path.
var flag = contentLib.setPermissions({
    key: '/features/js-libraries/mycontent',
    inheritPermissions: false,
    overwriteChildPermissions: true,
    permissions: [{
        principal: 'user:system:anonymous',
        allow: ['READ'],
        deny: ['DELETE']
    }]
});

if (flag) {
    log.info('Permissions set');
} else {
    log.info('Content not found');
}
// END

assert.assertTrue(flag);
