var assert = require('/lib/xp/testing.js');
var project = require('/lib/xp/project.js');

var addPermissionsExpected = {
    permissions: {
        owner: [
            'user:system:owner2',
            'user:system:owner1'
        ],
        viewer: [
            'user:system:viewer1'
        ]
    }
};

exports.addPermissions = function () {
    createProject();

    var result = project.addPermissions({
        id: 'myproject',
        permissions: {
            owner: [
                'user:system:owner2',
                'user:system:owner1'
            ],
            viewer: [
                'user:system:viewer1'
            ]
        }
    });


    assert.assertJsonEquals(addPermissionsExpected, result);
};

exports.addPermissionsNull = function () {
    createProject();

    var result = project.addPermissions({
        id: 'myproject',
        permissions: null
    });


    assert.assertJsonEquals({permissions: {}}, result);

    result = project.addPermissions({
        id: 'myproject',
        permissions: {owner: ['user:system:owner']}
    });


    assert.assertJsonEquals({permissions: {owner: ['user:system:owner']}}, result);

    result = project.addPermissions({
        id: 'myproject',
        permissions: {owner: null, author: null}
    });

    assert.assertJsonEquals({permissions: {owner: ['user:system:owner']}}, result);
};

var removePermissionsExpected = {
    permissions: {
        owner: [
            'user:system:owner3'
        ]
    }
};

exports.removePermissions = function () {
    createProject();

    addPermissions(
        'myproject',
        {
            owner: [
                'user:system:owner3',
                'user:system:owner2',
                'user:system:owner1'
            ],
            viewer: [
                'user:system:viewer1'
            ]

        });

    var result = removePermissions('myproject', {
        owner: [
            'user:system:owner2',
            'user:system:owner1'
        ],
        viewer: [
            'user:system:viewer1'
        ],
        contributor: [
            'user:system:contributor'
        ],
        author: [
            'user:system:author1', 'user:system:author2'
        ]

    });

    assert.assertJsonEquals(removePermissionsExpected, result);
};

exports.removePermissionsNull = function () {
    createProject();

    var result = project.removePermissions({
        id: 'myproject',
        permissions: null
    });


    assert.assertJsonEquals({permissions: {}}, result);

    result = project.addPermissions({
        id: 'myproject',
        permissions: {owner: ['user:system:owner']}
    });


    assert.assertJsonEquals({permissions: {owner: ['user:system:owner']}}, result);

    result = project.removePermissions({
        id: 'myproject',
        permissions: {owner: null, author: null}
    });

    assert.assertJsonEquals({permissions: {owner: ['user:system:owner']}}, result);
};

function addPermissions(projectName, permissions) {
    return project.addPermissions({
        id: projectName,
        permissions: permissions
    });
}

function removePermissions(projectName, permissions) {
    return project.removePermissions({
        id: projectName,
        permissions: permissions
    });
}

function createProject() {
    project.create({
        id: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        readAccess: {public: true}
    });
}
