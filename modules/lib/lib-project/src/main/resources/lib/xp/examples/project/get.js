// BEGIN
// Fetches an existing content project
var projectLib = require('/lib/xp/project');
var project = projectLib.get({
    id: 'my-project'
});
// END

// BEGIN
var expected = {
    'id': 'my-project',
    'displayName': 'My Content Project',
    'permissions': {
        'owner': [
            'user:mystore:user1'
        ],
        'editor': [
            'user:mystore:user2'
        ]
    },
    'readAccess': {
        'public': true
    }
};
// END
