var assert = require('/lib/xp/testing.js');
var project = require('/lib/xp/project.js');

var createProjectExpected = {
    id: 'myproject',
    displayName: 'project display name',
    description: 'project description',
    language: 'ja',
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
        public: true
    }
};

exports.createProject = function () {
    var result = project.create({
        id: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        language: 'ja',
        readAccess: {public: true},
        permissions: {
            owner: ['user:system:owner2', 'user:system:owner1'], viewer: ['user:system:viewer1']
        }
    });

    assert.assertJsonEquals(createProjectExpected, result);
};

var createProjectWithOneParentExpected = {
    id: 'myproject',
    displayName: 'project display name',
    description: 'project description',
    language: 'ja',
    parents: ['testparent'],
    parent: 'testparent',
    permissions: {},
    readAccess: {
        public: true
    }
};

exports.createProjectWithOneParent = function () {
    var result = project.create({
        id: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        language: 'ja',
        parents: ['testparent'],
        readAccess: {public: true},
        permissions: {}
    });

    assert.assertJsonEquals(createProjectWithOneParentExpected, result);
};

var createProjectWithoutLanguageExpected = {
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
        public: true
    }
};

exports.createProjectWithoutLanguage = function () {
    var result = project.create({
        id: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        readAccess: {public: true},
        permissions: {owner: ['user:system:owner2', 'user:system:owner1'], viewer: ['user:system:viewer1']}
    });

    assert.assertJsonEquals(createProjectWithoutLanguageExpected, result);
};

var createProjectWithoutPermissionsExpected = {

    id: 'myproject',
    displayName: 'project display name',
    description: 'project description',
    language: 'ja',
    parents: [],
    permissions: {},
    readAccess: {
        public: true
    }

};

exports.createProjectWithoutPermissions = function () {
    var result = project.create({
        id: 'myproject',
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
            id: 'myproject',
            displayName: 'project display name',
            description: 'project description',
            language: 'no',
            permissions: {owner: ['user:system:owner2', 'user:system:owner1'], viewer: ['user:system:viewer1']}
        });

        throw new Error('IllegalArgumentException should be thrown.');
    } catch (e) {
        assert.assertEquals('Invalid value for readAccess.', e.getMessage());
    }
};

var createProjectWithApplicationsExpected = {
    id: 'myproject',
    displayName: 'project display name',
    description: 'project description',
    parents: [],
    siteConfig: [
        {
            applicationKey: 'appKey1',
            config: {
                a: 'a',
                b: true
            }
        },
        {
            applicationKey: 'appKey2',
            config: {
                c: 4
            }
        }
    ],
    permissions: {},
    readAccess: {
        public: true
    }
};

exports.createProjectWithApplications = function () {
    var result = project.create({
        id: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        readAccess: {public: true},
        siteConfig: [
            {
                applicationKey: 'appKey1',
                config: {
                    a: 'a', b: true
                }
            }, {
                applicationKey: 'appKey2',
                config: {
                    c: 4, d: null
                }
            }]
    });

    assert.assertJsonEquals(createProjectWithApplicationsExpected, result);
};
