var assert = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');

exports.returnsIds = function () {

    var result = content.query({
        count: 10,
        parent: '/a/b',
        returns: 'ids'
    });

    assert.assertJsonEquals({
        total: 2,
        count: 2,
        hits: [
            {id: 'id1', score: 1.0},
            {id: 'id2', score: 0.5}
        ]
    }, result);
};

exports.returnsPaths = function () {

    var result = content.query({
        count: 10,
        parent: '/a/b',
        returns: 'paths'
    });

    assert.assertJsonEquals({
        total: 2,
        count: 2,
        hits: [
            {id: 'id1', path: '/a/b/one'},
            {id: 'id2', path: '/a/b/two'}
        ]
    }, result);
};

exports.returnsFields = function () {

    var result = content.query({
        count: 10,
        query: '_name = "one"',
        returns: ['_name', '_references']
    });

    assert.assertJsonEquals({
        total: 2,
        count: 2,
        hits: [
            {
                id: 'id1',
                score: 1.0,
                fields: {
                    // field keys are lowercase
                    _name: 'one',
                    '_references': ['ref-a', 'ref-b']
                }
            },
            {
                id: 'id2',
                score: 0.5
            }
        ]
    }, result);
};

exports.returnsInvalid = function () {

    try {
        content.query({
            count: 10,
            returns: 'everything'
        });
    } catch (e) {
        assert.assertEquals('returns must be \'contents\', \'ids\', \'paths\' or an array of index field names', e.getMessage());
        return;
    }

    throw {message: 'Expected exception'};
};
