var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Creates a group.
var role = authLib.createRole({
    name: 'aRole',
    displayName: 'Role Display Name',
    description: 'description'
});
// END

// BEGIN
// Information about the created group.
var expected = {
    'type': 'role',
    'key': 'role:aRole',
    'displayName': 'Role Display Name',
    'modifiedTime': '1970-01-01T00:00:00Z',
    'description': 'description'
};
// END

t.assertJsonEquals(expected, role);
