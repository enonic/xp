var t = require('/lib/xp/testing.js');
var auth = require('/lib/xp/auth.js');

exports.getUserstorePermissions = function () {

    var result = auth.getUserstorePermissions({key: 'myUserStore'});

    var expectedJson = [
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

    t.assertJsonEquals(expectedJson, result);

};

exports.getNonExistingUserstorePermissions = function () {

    var result = auth.getUserstorePermissions({key: 'myUserStore'});

    var length = result.length;

    t.assertEquals(0, length);

};
