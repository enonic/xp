var assert = require('/lib/xp/testing.js');
var project = require('/lib/xp/project.js');

exports.setPublicRead = function () {
    createProject();

    var result = project.setPublicRead({
        id: 'myproject',
        publicRead: false
    });

    assert.assertJsonEquals(false, result);

    result = project.setPublicRead({
        id: 'myproject',
        publicRead: true
    });

    assert.assertJsonEquals(true, result);
};

function createProject() {
    project.create({
        id: 'myproject',
        displayName: 'project display name',
        description: 'project description',
        publicRead: true,
        permissions: {owner: ['user:system:owner2', 'user:system:owner1'], viewer: ['user:system:viewer1']}
    });
}
