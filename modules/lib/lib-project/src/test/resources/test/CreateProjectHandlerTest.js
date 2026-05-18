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
    publicRead: true
};

exports.createProject = function () {
    var result = project.create({
        id: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        language: 'ja',
        publicRead: true,
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
    publicRead: true
};

exports.createProjectWithOneParent = function () {
    var result = project.create({
        id: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        language: 'ja',
        parents: ['testparent'],
        publicRead: true,
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
    publicRead: true
};

exports.createProjectWithoutLanguage = function () {
    var result = project.create({
        id: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        publicRead: true,
        permissions: {owner: ['user:system:owner2', 'user:system:owner1'], viewer: ['user:system:viewer1']}
    });

    assert.assertJsonEquals(createProjectWithoutLanguageExpected, result);
};

var createProjectWithoutPermissionsExpected = {

    id: 'myproject',
    displayName: 'project display name',
    description: 'project description',
    language: 'no',
    parents: [],
    permissions: {},
    publicRead: true

};

exports.createProjectWithoutPermissions = function () {
    var result = project.create({
        id: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        language: 'no',
        publicRead: true
    });

    assert.assertJsonEquals(createProjectWithoutPermissionsExpected, result);
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
    publicRead: true
};

exports.createProjectWithApplications = function () {
    var result = project.create({
        id: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        publicRead: true,
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
