var assert = require('/lib/xp/testing.js');
var project = require('/lib/xp/project.js');

exports.deleteProject = function () {
    createProject();

    var result = project.delete({
        id: 'myproject'
    });

    assert.assertJsonEquals(true, result);
};

exports.deleteNotExistProject = function () {

    var result = project.delete({
        id: 'myproject'
    });

    assert.assertJsonEquals(false, result);
};


exports.deleteProjectNull = function () {
    try {

        project.delete({
            id: null
        });
        throw new Error('IllegalArgumentException should be thrown.');

    } catch (e) {
        assert.assertEquals('Project name is required', e.getMessage());

    }
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
