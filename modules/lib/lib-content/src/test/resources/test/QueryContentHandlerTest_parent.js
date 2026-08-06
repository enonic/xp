var assert = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');

exports.parentByPath = function () {

    var result = content.query({
        'count': 10,
        'parent': '/a/b',
        'sort': '_manualordervalue DESC'
    });

    assert.assertEquals(20, result.total);
    assert.assertEquals(3, result.count);
};

exports.parentById = function () {

    var result = content.query({
        'count': 10,
        'parent': '123456'
    });

    assert.assertEquals(20, result.total);
    assert.assertEquals(3, result.count);
};

exports.parentNotFound = function () {

    var result = content.query({
        'count': 10,
        'parent': 'unknown-id'
    });

    assert.assertEquals(0, result.total);
    assert.assertEquals(0, result.count);
    assert.assertEquals(0, result.hits.length);
};
