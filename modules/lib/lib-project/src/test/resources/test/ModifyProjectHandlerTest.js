var assert = require('/lib/xp/testing.js');
var project = require('/lib/xp/project.js');

var modifyProjectExpected = {
    id: 'myproject',
    displayName: 'new display name',
    description: 'new description',
    language: 'ja',
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

exports.modifyProject = function () {
    createProject();

    var result = project.modify({
        id: 'myproject',
        editor: function (p) {
            p.displayName = 'new display name';
            p.description = 'new description';
            p.language = 'ja';
            p.siteConfig = [
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
            ];
            return p;
        }
    });


    assert.assertJsonEquals(modifyProjectExpected, result);
};

var modifyDescriptionExpected = {
    id: 'myproject',
    displayName: 'project display name',
    description: 'new description',
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

exports.modifyDescription = function () {
    createProject();

    var result = project.modify({
        id: 'myproject',
        editor: function (p) {
            p.description = 'new description';
            return p;
        }
    });

    assert.assertJsonEquals(modifyDescriptionExpected, result);
};

var modifyDisplayNameExpected = {
    id: 'myproject',
    displayName: 'new display name',
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

exports.modifyDisplayName = function () {
    createProject();

    var result = project.modify({
        id: 'myproject',
        editor: function (p) {
            p.displayName = 'new display name';
            return p;
        }
    });

    assert.assertJsonEquals(modifyDisplayNameExpected, result);
};

var modifyLanguageExpected = {
    id: 'myproject',
    displayName: 'project display name',
    description: 'project description',
    language: 'no',
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

var clearLanguageExpected = {
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

var modifyApplicationsExpected = {
    id: 'myproject',
    displayName: 'project display name',
    description: 'project description',
    language: 'ja',
    parents: [],
    siteConfig: [
        {
            applicationKey: 'appKey2',
            config: {
                c: 4
            }
        }
    ],
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

exports.modifyLanguage = function () {
    createProject();

    var result = project.modify({
        id: 'myproject',
        editor: function (p) {
            p.language = 'no';
            return p;
        }
    });

    assert.assertJsonEquals(modifyLanguageExpected, result);
};

exports.clearLanguage = function () {
    createProject();

    var result = project.modify({
        id: 'myproject',
        editor: function (p) {
            p.language = null;
            return p;
        }
    });

    assert.assertJsonEquals(clearLanguageExpected, result);
};

exports.modifyApplications = function () {
    createProject();

    var result = project.modify({
        id: 'myproject',
        editor: function (p) {
            p.siteConfig = [
                {
                    applicationKey: 'appKey2',
                    config: {
                        c: 4
                    }
                }
            ];
            return p;
        }
    });

    assert.assertJsonEquals(modifyApplicationsExpected, result);
};

function createProject() {
    project.create({
        id: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        language: 'ja',
        publicRead: true,
        permissions: {owner: ['user:system:owner2', 'user:system:owner1'], viewer: ['user:system:viewer1']}
    });
}
