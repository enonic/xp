var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

exports.parentByPath = function () {

    var result = repo.query({
        count: 10,
        parent: '/parent'
    });

    assert.assertEquals(2, result.total);
};

exports.parentById = function () {

    var result = repo.query({
        count: 10,
        parent: 'parent-id',
        recursive: true
    });

    assert.assertEquals(2, result.total);
};

exports.parentNotFound = function () {

    var result = repo.query({
        count: 10,
        parent: 'unknown-id'
    });

    assert.assertEquals(0, result.total);
    assert.assertEquals(0, result.count);
    assert.assertEquals(0, result.hits.length);
};

exports.returnFields = function () {

    var result = repo.query({
        count: 10,
        query: '_name = "my-node"',
        returns: ['_path', '_nodeType']
    });

    assert.assertJsonEquals({
        total: 1,
        count: 1,
        hits: [
            {
                id: 'node-id',
                score: 1.0,
                fields: {
                    '_path': '/my-node',
                    '_nodeType': 'default'
                }
            }
        ]
    }, result);
};

exports.multiValuedReturnField = function () {

    var result = repo.query({
        count: 10,
        query: '_name = "my-node"',
        returns: ['_path']
    });

    assert.assertJsonEquals({
        total: 1,
        count: 1,
        hits: [
            {
                id: 'node-id',
                score: 1.0,
                fields: {
                    '_path': ['/my-node', '/my-other-node']
                }
            }
        ]
    }, result);
};

exports.returnsEmptyArray = function () {

    try {
        repo.query({
            count: 10,
            returns: []
        });
    } catch (e) {
        assert.assertEquals('returns must name at least one index field', e.getMessage());
        return;
    }

    throw {message: 'Expected exception'};
};
