var authLib = require('/lib/xp/auth');
var assert = require('/lib/xp/assert');

// BEGIN
// Find principals with the specified name.
var result = authLib.findPrincipals({
    type: 'user',
    userStore: 'user-store',
    start: 0,
    count: 10,
    name: 'user1'
});
// END

// BEGIN
// Result for finding principals.
var expected = {
    "total": 3,
    "count": 3,
    "hits": [
        {
            "type": "group",
            "key": "group:system:group-a",
            "displayName": "Group A",
            "modifiedTime": "1970-01-01T00:00:00Z",
            "description": "description"
        },
        {
            "type": "role",
            "key": "role:aRole",
            "displayName": "Role Display Name",
            "modifiedTime": "1970-01-01T00:00:00Z",
            "description": "description"
        },
        {
            "type": "user",
            "key": "user:enonic:user1",
            "displayName": "User 1",
            "modifiedTime": "1970-01-01T00:00:00Z",
            "disabled": false,
            "email": "user1@enonic.com",
            "login": "user1",
            "userStore": "enonic"
        }
    ]
};
// END

assert.assertJsonEquals(expected, result);
