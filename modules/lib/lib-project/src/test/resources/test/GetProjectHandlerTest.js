var assert = require('/lib/xp/testing.js');
var project = require('/lib/xp/project.js');

var getProjectExpected = {
    id: 'myproject',
    displayName: 'project display name',
    description: 'project description',
    parents: [],
    permissions: {
        owner: [
            'user:system:owner2',
            'user:system:owner1'
        ],
        viewer: [
            'user:system:viewer1'
        ]
    },
    readAccess: {
        'public': true
    }
};

exports.getProject = function () {
    createProject({owner: ['user:system:owner2', 'user:system:owner1'], viewer: ['user:system:viewer1']});

    var result = project.get({
        id: 'myproject'
    });

    assert.assertJsonEquals(getProjectExpected, result);
};

var getProjectWithoutPermissionsExpected = {
    id: 'myproject',
    displayName: 'project display name',
    description: 'project description',
    parents: [],
    permissions: {},
    readAccess: {
        'public': true
    }
};

exports.getProjectWithoutPermissions = function () {
    createProject(null);

    var result = project.get({
        id: 'myproject'
    });

    assert.assertJsonEquals(getProjectWithoutPermissionsExpected, result);
};

exports.getProjectNotExist = function () {
    createProject();

    var result = project.get({
        id: 'myproject1'
    });

    assert.assertJsonEquals(null, result);
};

exports.getProjectNull = function () {
    createProject();

    try {
        project.get({
            id: null
        });

        throw new Error('IllegalArgumentException should be thrown.');

    } catch (e) {
        assert.assertEquals('Project name is required', e.getMessage());
    }
};


function createProject(permissions) {
    project.create({
        id: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        readAccess: {public: true},
        permissions: permissions
    });
}
