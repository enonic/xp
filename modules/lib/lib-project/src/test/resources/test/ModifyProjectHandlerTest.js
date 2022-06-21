var assert = require('/lib/xp/testing.js');
var project = require('/lib/xp/project.js');

var modifyProjectExpected = {
    id: 'myproject',
    displayName: 'new display name',
    description: 'new description',
    language: 'fr',
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
    readAccess: {
        public: true
    }
};

exports.modifyProject = function () {
    createProject();

    var result = project.modify({
        id: 'myproject',
        displayName: 'new display name',
        description: 'new description',
        language: 'fr',
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
        ]
    });


    assert.assertJsonEquals(modifyProjectExpected, result);
};

var modifyDescriptionExpected = {
    id: 'myproject',
    displayName: 'project display name',
    description: 'new description',
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

exports.modifyDescription = function () {
    createProject();

    var result = project.modify({
        id: 'myproject',
        description: 'new description'
    });

    assert.assertJsonEquals(modifyDescriptionExpected, result);
};

var modifyDisplayNameExpected = {
    id: 'myproject',
    displayName: 'new display name',
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
        public: true
    }
};

exports.modifyDisplayName = function () {
    createProject();

    var result = project.modify({
        id: 'myproject',
        displayName: 'new display name'
    });

    assert.assertJsonEquals(modifyDisplayNameExpected, result);
};

var modifyLanguageExpected = {
    id: 'myproject',
    displayName: 'project display name',
    description: 'project description',
    language: 'no',
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

var modifyApplicationsExpected = {
    id: 'myproject',
    displayName: 'project display name',
    description: 'project description',
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
    readAccess: {
        public: true
    }
};

exports.modifyLanguage = function () {
    createProject();

    var result = project.modify({
        id: 'myproject',
        language: 'no'
    });

    assert.assertJsonEquals(modifyLanguageExpected, result);
};

exports.modifyApplications = function () {
    createProject();

    var result = project.modify({
        id: 'myproject',
        siteConfig: [
            {
                applicationKey: 'appKey2',
                config: {
                    c: 4
                }
            }
        ]
    });

    assert.assertJsonEquals(modifyApplicationsExpected, result);
};

function createProject() {
    project.create({
        id: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        readAccess: {public: true},
        permissions: {owner: ['user:system:owner2', 'user:system:owner1'], viewer: ['user:system:viewer1']}
    });
}
