var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Returns all members for specified principal key.
var members = authLib.getMembers('group:system:group-a');
// END

// BEGIN
// Information when getting a principal.
var expected = [
    {
        'type': 'user',
        'key': 'user:enonic:user1',
        'displayName': 'User 1',
        'modifiedTime': '1970-01-01T00:00:00Z',
        'disabled': false,
        'email': 'user1@enonic.com',
        'login': 'user1',
        'idProvider': 'enonic',
        'hasPassword': false
    },
    {
        'type': 'user',
        'key': 'user:enonic:user2',
        'displayName': 'User 2',
        'modifiedTime': '1970-01-01T00:00:00Z',
        'disabled': false,
        'email': 'user2@enonic.com',
        'login': 'user2',
        'idProvider': 'enonic',
        'hasPassword': false
    }
];
// END

t.assertJsonEquals(expected, members);
