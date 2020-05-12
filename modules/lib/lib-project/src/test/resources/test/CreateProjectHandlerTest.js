var assert = require('/lib/xp/testing.js');
var project = require('/lib/xp/project.js');

var createProjectExpected = {
    name: "myproject",
    displayName: "project display name",
    description: "project description",
    language: "no",
    permissions: {
        owner: [
            "user:system:owner2",
            "user:system:owner1"
        ],
        viewer: [
            "user:system:viewer1"
        ]
    },
    readAccess: {
        public: true
    }
};

exports.createProject = function () {
    var result = project.create({
        name: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        language: 'no',
        readAccess: {public: true},
        permissions: {owner: ['user:system:owner2', 'user:system:owner1'], viewer: ['user:system:viewer1']}
    });

    assert.assertJsonEquals(createProjectExpected, result);
};

var createProjectWithoutLanguageExpected = {
    name: "myproject",
    displayName: "project display name",
    description: "project description",
    permissions: {
        owner: [
            "user:system:owner2",
            "user:system:owner1"
        ],
        viewer: [
            "user:system:viewer1"
        ]
    },
    readAccess: {
        public: true
    }
};

exports.createProjectWithoutLanguage = function () {
    var result = project.create({
        name: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        readAccess: {public: true},
        permissions: {owner: ['user:system:owner2', 'user:system:owner1'], viewer: ['user:system:viewer1']}
    });

    assert.assertJsonEquals(createProjectWithoutLanguageExpected, result);
};

var createProjectWithoutPermissionsExpected = {

    name: "myproject",
    displayName: "project display name",
    description: "project description",
    language: "no",
    permissions: {},
    readAccess: {
        public: true
    }

};

exports.createProjectWithoutPermissions = function () {
    var result = project.create({
        name: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        language: 'no',
        readAccess: {public: true}
    });

    assert.assertJsonEquals(createProjectWithoutPermissionsExpected, result);
};

exports.createProjectWithoutReadAccess = function () {

    try {
        project.create({
            name: 'myproject',
            displayName: 'project display name',
            description: 'project description',
            language: 'no',
            permissions: {owner: ['user:system:owner2', 'user:system:owner1'], viewer: ['user:system:viewer1']}
        });

        throw new Error('IllegalArgumentException should be thrown.');
    } catch (e) {
        assert.assertEquals('Invalid value for readAccess.', e.message);
    }
};
