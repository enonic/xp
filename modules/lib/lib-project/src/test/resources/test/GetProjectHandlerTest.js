var assert = require('/lib/xp/testing.js');
var project = require('/lib/xp/project.js');

var getProjectExpected = {
    name: 'myproject',
    displayName: 'project display name',
    description: 'project description',
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
        name: 'myproject'
    });

    assert.assertJsonEquals(getProjectExpected, result);
};

var getProjectWithoutPermissionsExpected = {
    name: 'myproject',
    displayName: 'project display name',
    description: 'project description',
    permissions: {},
    readAccess: {
        'public': true
    }
};

exports.getProjectWithoutPermissions = function () {
    createProject(null);

    var result = project.get({
        name: 'myproject'
    });

    assert.assertJsonEquals(getProjectWithoutPermissionsExpected, result);
};

exports.getProjectNotExist = function () {
    createProject();

    var result = project.get({
        name: 'myproject1'
    });

    assert.assertJsonEquals(null, result);
};

exports.getProjectNull = function () {
    createProject();

    try {
        project.get({
            name: null
        });

        throw new Error('IllegalArgumentException should be thrown.');

    } catch (e) {
        assert.assertEquals('Project name cannot be null', e.message);
    }
};


function createProject(permissions) {
    project.create({
        name: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        readAccess: {public: true},
        permissions: permissions
    });
}
