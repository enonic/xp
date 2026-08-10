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

exports.returnsPathField = function () {

    var result = content.query({
        count: 10,
        parent: '/a/b',
        returns: ['_path']
    });

    assert.assertJsonEquals({
        total: 2,
        count: 2,
        hits: [
            {id: 'id1', score: 0, fields: {_path: '/a/b/one'}},
            {id: 'id2', score: 0, fields: {_path: '/a/b/two'}}
        ]
    }, result);
};

exports.returnsFields = function () {

    var result = content.query({
        count: 10,
        query: '_name = "one"',
        returns: ['_name', 'displayName']
    });

    assert.assertJsonEquals({
        total: 2,
        count: 2,
        hits: [
            {
                id: 'id1',
                score: 1.0,
                fields: {
                    // fields come back under the names a content shows them by
                    _name: 'one',
                    displayName: 'One'
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
        assert.assertEquals('returns must be \'contents\', \'ids\' or an array of index field names', e.getMessage());
        return;
    }

    throw {message: 'Expected exception'};
};

exports.returnsEmptyArray = function () {

    try {
        content.query({
            count: 10,
            returns: []
        });
    } catch (e) {
        assert.assertEquals('returns must name at least one index field, or be \'contents\' or \'ids\'', e.getMessage());
        return;
    }

    throw {message: 'Expected exception'};
};

exports.returnsFieldAbsentFromHit = function () {

    var result = content.query({
        count: 10,
        query: '_name = "one"',
        returns: ['_name', 'language']
    });

    assert.assertJsonEquals({
        total: 1,
        count: 1,
        hits: [
            {
                id: 'id1',
                score: 0,
                fields: {
                    '_name': 'one'
                }
            }
        ]
    }, result);
};
