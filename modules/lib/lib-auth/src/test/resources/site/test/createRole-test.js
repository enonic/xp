var t = require('/lib/xp/testing.js');
var authLib = require('/lib/xp/auth.js');

function createRole() {
    return authLib.createRole({
        name: 'aRole',
        displayName: 'Role Display Name',
        description: 'description'
    });
}

exports.createRole = function () {

    var result = createRole();

    var expectedJson = {
        'type': 'role',
        'key': 'role:aRole',
        'displayName': 'Role Display Name',
        'modifiedTime': '1970-01-01T00:00:00Z',
        'description': 'description'
    };

    t.assertJsonEquals(expectedJson, result);

};

exports.createRoleUnAuthenticated = function () {

    var result = createRole();

    var expectedJson = null;

    t.assertJsonEquals(expectedJson, result);
};
