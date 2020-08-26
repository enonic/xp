// BEGIN
// Removes permissions of an existing content project
var projectLib = require('/lib/xp/project');

projectLib.addPermissions({
    id: 'my-project',
    permissions: {
        owner: ['user:mystore:user1', 'user:mystore:user2'],
        editor: ['user:mystore:user3']
    }
});

var currentPermissions = projectLib.removePermissions({
    id: 'my-project',
    permissions: {
        owner: ['user:mystore:user2']
    }
});
// END

// BEGIN
var expected = {
    'id': 'my-project',
    'permissions': {
        'owner': [
            'user:mystore:user1'
        ],
        'editor': [
            'user:mystore:user3'
        ]
    }
};
// END

