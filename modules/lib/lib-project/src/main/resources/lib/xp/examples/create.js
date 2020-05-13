var projectLib = require('/lib/xp/project');
var assert = require('/lib/xp/testing');

// Creates a Content Project with minimal properties
var project1 = projectLib.create({
    id: 'my-project',
    displayName: 'My Content Project',
    readAccess: {
        public: true
    }
});

// Creates a Content Project with extended properties
var project2 = projectLib.create({
    id: 'my-project',
    displayName: 'My Content Project',
    description: 'Some exciting content is stored here',
    language: 'no',
    permissions: {
        owner: 'user:mystore:user1',
        editor: 'user:mystore:user2',
        author: 'user:mystore:user3',
        contributor: 'user:mystore:user4',
        viewer: 'user:mystore:user5'
    },
    readAccess: {
        public: false
    }
});

// First project created.
var expected1 = {
    'id': 'my-project',
    'displayName': 'My Content Project',
    'permissions': {},
    'readAccess': {}
};
assert.assertJsonEquals(expected1, result1);

var expected2 =
    {
        'id': 'test-repo2',
        'branches': [
            'master'
        ],
        'settings': {},
        'data': {}
    };
assert.assertJsonEquals(expected2, result2);
