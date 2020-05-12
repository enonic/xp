var assert = require('/lib/xp/testing.js');
var project = require('/lib/xp/project.js');

exports.modifyReadAccess = function () {
    createProject();

    var result = project.modifyReadAccess({
        name: 'myproject',
        readAccess: {public: false}
    });

    assert.assertJsonEquals({readAccess: {public: false}}, result);

    result = project.modifyReadAccess({
        name: 'myproject',
        readAccess: {public: true}
    });

    assert.assertJsonEquals({readAccess: {public: true}}, result);
};

exports.modifyReadAccessNull = function () {
    createProject();

    try {
        var result = project.modifyReadAccess({
            name: 'myproject',
            readAccess: null
        });

        throw new Error('IllegalArgumentException should be thrown.');
    } catch (e) {
        assert.assertEquals('Invalid value for readAccess.', e.message);
    }

    try {
        var result = project.modifyReadAccess({
            name: 'myproject',
            readAccess: {public: null}
        });

        throw new Error('IllegalArgumentException should be thrown.');
    } catch (e) {
        assert.assertEquals('Invalid value for readAccess.', e.message);
    }
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
