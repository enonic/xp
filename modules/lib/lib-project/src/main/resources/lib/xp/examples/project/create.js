// BEGIN
// Creates a Content Project with minimal properties
var projectLib = require('/lib/xp/project');
try {
    var project = projectLib.create({
        id: 'my-project',
        displayName: 'My Content Project',
        publicRead: true
    });
} catch (e) {
    log.error('Failed to create a project: ' + e);
}
// END

// BEGIN
var expected = {
    'id': 'my-project',
    'displayName': 'My Content Project',
    'permissions': [],
    'publicRead': true
};
// END

// BEGIN
// Creates a Content Project with extended properties
var projectLib2 = require('/lib/xp/project');
var project2 = projectLib2.create({
    id: 'my-project',
    displayName: 'My Content Project',
    description: 'Some exciting content is stored here',
    language: 'no',
    permissions: {
        owner: ['user:mystore:user1'],
        editor: ['user:mystore:user2'],
        author: ['user:mystore:user3'],
        contributor: ['user:mystore:user4'],
        viewer: ['user:mystore:user5']
    },
    publicRead: false
});
// END

// BEGIN
var expected2 = {
    'id': 'my-project',
    'displayName': 'My Content Project',
    'description': 'Some exciting content is stored here',
    'language': 'no',
    'permissions': {
        'owner': [
            'user:mystore:user1'
        ],
        'editor': [
            'user:mystore:user2'
        ],
        'author': [
            'user:mystore:user3'
        ],
        'contributor': [
            'user:mystore:user4'
        ],
        'viewer': [
            'user:mystore:user5'
        ]
    },
    'publicRead': false
};
// END

// BEGIN
// Creates a Content Project inside context with `system.admin` role
var projectLib3 = require('/lib/xp/project');
var contextLib = require('/lib/xp/context');

var createProject = function () {
    return projectLib3.create({
        id: 'my-project',
        displayName: 'My Content Project',
        publicRead: true
    });
};

var project3 = contextLib.run({
    principals: ['role:system.admin']
}, createProject);
// END
