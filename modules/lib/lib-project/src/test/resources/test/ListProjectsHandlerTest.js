var assert = require('/lib/xp/testing.js');
var project = require('/lib/xp/project.js');

var project1Expected = {
    name: 'myproject1',
    displayName: 'project display name 1',
    description: 'project description 1',
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
        public: false
    }
};

var project2Expected = {
    name: 'myproject2',
    displayName: 'project display name 2',
    description: 'project description 2',
    permissions: {},
    readAccess: {public: false}
};

exports.listProjects = function () {
    assert.assertJsonEquals([], project.list());

    createProject1();
    assert.assertJsonEquals([project1Expected], project.list());

    createProject2();
    assert.assertJsonEquals([project1Expected, project2Expected], project.list());
};

function createProject1() {
    project.create({
        name: 'myproject1',
        displayName: 'project display name 1',
        description: 'project description 1',
        readAccess: {public: false},
        permissions: {
            owner: ['user:system:owner2', 'user:system:owner1'],
            viewer: ['user:system:viewer1']
        }
    });
}

function createProject2() {
    project.create({
        name: 'myproject2',
        displayName: 'project display name 2',
        description: 'project description 2',
        readAccess: {public: false}
    });
}

