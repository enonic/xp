var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Set permissions for content by path.
var flag = contentLib.applyPermissions({
    key: '/features/js-libraries/mycontent',
    permissions: [{
        principal: 'user:system:anonymous',
        allow: ['READ'],
        deny: ['DELETE']
    }]
});

if (flag) {
    log.info('Permissions applied');
} else {
    log.info('Content not found');
}
// END

assert.assertTrue(flag);
