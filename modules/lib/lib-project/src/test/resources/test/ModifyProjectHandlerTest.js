var assert = require('/lib/xp/testing.js');
var project = require('/lib/xp/project.js');

var modifyProjectExpected = {
    name: "myproject",
    displayName: "new display name",
    description: "new description",
    language: "fr",
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

exports.modifyProject = function () {
    createProject();

    var result = project.modify({
        name: 'myproject',
        displayName: 'new display name',
        description: 'new description',
        language: 'fr'
    });


    assert.assertJsonEquals(modifyProjectExpected, result);
};

var modifyDescriptionExpected = {
    name: "myproject",
    displayName: "project display name",
    description: "new description",
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

exports.modifyDescription = function () {
    createProject();

    var result = project.modify({
        name: 'myproject',
        description: 'new description'
    });

    assert.assertJsonEquals(modifyDescriptionExpected, result);
};

var modifyDisplayNameExpected = {
    name: "myproject",
    displayName: "new display name",
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

exports.modifyDisplayName = function () {
    createProject();

    var result = project.modify({
        name: 'myproject',
        displayName: 'new display name'
    });

    assert.assertJsonEquals(modifyDisplayNameExpected, result);
};

var modifyLanguageExpected = {
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

exports.modifyLanguage = function () {
    createProject();

    var result = project.modify({
        name: 'myproject',
        language: 'no'
    });

    assert.assertJsonEquals(modifyLanguageExpected, result);
};

function createProject() {
    project.create({
        name: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        readAccess: {public: true},
        permissions: {owner: ['user:system:owner2', 'user:system:owner1'], viewer: ['user:system:viewer1']}
    });
}
