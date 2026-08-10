var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var multiRepoConnection = nodeLib.multiRepoConnect({
    sources: [
        {
            repoId: 'my-repo',
            branch: 'master',
            principals: ['role:system.admin']
        },
        {
            repoId: 'com.enonic.cms.default',
            branch: 'draft',
            principals: ['role:system.admin']
        }
    ]
});

exports.parentByPath = function () {

    var result = multiRepoConnection.query({
        count: 10,
        parent: '/parent',
        recursive: true
    });

    assert.assertEquals(2, result.total);
};

exports.parentById = function () {

    multiRepoConnection.query({
        count: 10,
        parent: 'parent-id'
    });
};

exports.recursiveWithoutParent = function () {

    multiRepoConnection.query({
        count: 10,
        recursive: true
    });
};
