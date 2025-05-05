var assert = require('/lib/xp/testing.js');
var contentLib = require('/lib/xp/content.js');

exports.applyPermissionsMissingPrincipals = function () {

    assert.assertThrows(() => contentLib.applyPermissions({
        key: '/features/js-libraries/mycontent',
        permissions: [{
            principal: 'user:system:anonymous',
            allow: ['READ'],
            deny: ['DELETE']
        }]
    }));
};

exports.applyPermissionsNotFoundByPath = function () {

    assert.assertThrows(() => contentLib.applyPermissions({
        key: '/features/js-libraries/mycontent',
        permissions: [{
            principal: 'user:system:anonymous',
            allow: ['READ'],
            deny: ['DELETE']
        }],
    }));
};

exports.applyPermissionsNotFoundById = function () {

    assert.assertThrows(() => contentLib.applyPermissions({
        key: '/features/js-libraries/mycontent',
        permissions: [{
            principal: 'user:system:anonymous',
            allow: ['READ'],
            deny: ['DELETE']
        }]
    }));
};

exports.applyPermissionsNonCompatible = function () {

    assert.assertThrows(() => contentLib.applyPermissions({
        key: '/features/js-libraries/mycontent',
        permissions: [{
            principal: 'user:system:anonymous',
            allow: ['READ']
        }],
        addPermissions: [{
            principal: 'user:system:anonymous',
            allow: ['MODIFY']
        }]
    }));
};

exports.applyPermissionsAddRemove = function () {

    var result = contentLib.applyPermissions({
        key: '/features/js-libraries/mycontent',
        addPermissions: [{
            principal: 'user:system:anonymous',
            allow: ['READ']
        }, {
            principal: 'role:system.everyone',
            allow: ['READ']
        }],
        removePermissions: [{
            principal: 'user:system:anonymous',
            allow: ['MODIFY', 'DELETE']
        }]
    });

    assert.assertEquals(1, result['123456'].branchResults.length);
};

exports.applyPermissionsTreeScope = function () {

    var result = contentLib.applyPermissions({
        key: '/features/js-libraries/mycontent',
        permissions: [{
            principal: 'user:system:anonymous',
            allow: ['READ']
        }],
        scope: "TREE"
    });
};
