var authLib = require('/lib/xp/auth');
var assert = require('/lib/xp/assert');

// BEGIN
// Returns all memberships for specified principal key.
var memberships = authLib.getMemberships('user:myUserStore:userId');
// END

// BEGIN
// Information when getting a principal.
var expected = [
    {
        "type": "role",
        "key": "role:aRole",
        "displayName": "Role Display Name",
        "modifiedTime": "1970-01-01T00:00:00Z",
        "description": "description"
    },
    {
        "type": "group",
        "key": "group:system:group-a",
        "displayName": "Group A",
        "modifiedTime": "1970-01-01T00:00:00Z",
        "description": "description"
    }
];
// END

assert.assertJsonEquals(expected, memberships);
