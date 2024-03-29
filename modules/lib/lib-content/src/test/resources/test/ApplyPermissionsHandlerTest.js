var assert = require('/lib/xp/testing.js');
var contentLib = require('/lib/xp/content.js');

exports.applyPermissionsMissingPrincipals = function () {

    var result = contentLib.applyPermissions({
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

exports.applyPermissionsNotFoundByPath = function () {

    var result = contentLib.applyPermissions({
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

exports.applyPermissionsNotFoundById = function () {

    var result = contentLib.applyPermissions({
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
