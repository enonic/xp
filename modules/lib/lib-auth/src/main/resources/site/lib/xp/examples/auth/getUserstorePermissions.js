var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

var permissions = authLib.getUserstorePermissions({key: 'myUserStore'});

var expected = [
    {
        principal: {
            'type': 'user',
            'key': 'user:myUserStore:user',
            'displayName': 'User 1',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'disabled': false,
            'email': 'user1@enonic.com',
            'login': 'user1',
            'userStore': 'myUserStore'
        },
        access: 'ADMINISTRATOR'
    },
    {
        principal: {
            'type': 'group',
            'key': 'group:myUserStore:group',
            'displayName': 'Group A',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'description': 'description'
        },
        access: 'CREATE_USERS'
    }
];

t.assertJsonEquals(expected, permissions);
