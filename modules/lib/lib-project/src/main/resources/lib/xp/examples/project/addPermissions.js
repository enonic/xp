// BEGIN
// Adds permissions to an existing content project
var projectLib = require('/lib/xp/project');

var currentPermissions = projectLib.addPermissions({
    id: 'my-project',
    permissions: {
        owner: ['user:mystore:user1', 'user:mystore:user2'],
        editor: ['user:mystore:user3']
    }
});
// END

// BEGIN
var expected = {
    'id': 'my-project',
    'permissions': {
        'owner': [
            'user:mystore:user1',
            'user:mystore:user2'
        ],
        'editor': [
            'user:mystore:user3'
        ]
    }
};
// END

