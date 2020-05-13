// BEGIN
// Creates a Content Project with minimal properties
var projectLib = require('/lib/xp/project');
try {
    var project = projectLib.create({
        id: 'my-project',
        displayName: 'My Content Project',
        readAccess: {
            public: true
        }
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
    'readAccess': {
        'public': true
    }
};
// END

// BEGIN
// Creates a Content Project with extended properties
var projectLib = require('/lib/xp/project');
var project = projectLib.create({
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
    readAccess: {
        public: false
    }
});
// END

// BEGIN
var expected = {
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
    readAccess: {
        'public': false
    }
};
// END

// BEGIN
// Creates a Content Project inside context with `system.admin` role
var projectLib = require('/lib/xp/project');
var contextLib = require('/lib/xp/context');

var createProject = function () {
    return projectLib.create({
        id: 'my-project',
        displayName: 'My Content Project',
        readAccess: {
            public: true
        }
    });
}

var project = contextLib.run({
    principals: ["role:system.admin"]
}, createProject);
// END
