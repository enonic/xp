var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Return permissions for content by path.
var result = contentLib.getPermissions({
    key: '/features/js-libraries/mycontent'
});

if (result) {
    log.info('Content permissions: ' + JSON.stringify(result.permissions));
} else {
    log.info('Content not found');
}
// END

// BEGIN
// Permissions returned.
var expected = {
    'permissions': [
        {
            'principal': 'user:system:anonymous',
            'allow': [
                'READ'
            ],
            'deny': []
        }
    ]
};
// END

assert.assertJsonEquals(expected, result);
