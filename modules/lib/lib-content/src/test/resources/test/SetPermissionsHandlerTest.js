var assert = require('/lib/xp/testing.js');
var contentLib = require('/lib/xp/content.js');

exports.setPermissionsMissingPrincipals = function () {

    var result = contentLib.setPermissions({
        key: '/features/js-libraries/mycontent',
        overwriteChildPermissions: true,
        permissions: [{
            principal: 'user:system:anonymous',
            allow: ['READ'],
            deny: ['DELETE']
        }]
    });

    assert.assertEquals(false, result);
};

exports.setPermissionsNotFoundByPath = function () {

    var result = contentLib.setPermissions({
        key: '/features/js-libraries/mycontent',
        overwriteChildPermissions: true,
        permissions: [{
            principal: 'user:system:anonymous',
            allow: ['READ'],
            deny: ['DELETE']
        }],
    });

    assert.assertEquals(false, result);
};

exports.setPermissionsNotFoundById = function () {

    var result = contentLib.setPermissions({
        key: '/features/js-libraries/mycontent',
        overwriteChildPermissions: true,
        permissions: [{
            principal: 'user:system:anonymous',
            allow: ['READ'],
            deny: ['DELETE']
        }]
    });

    assert.assertEquals(false, result);
};
