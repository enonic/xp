var assert = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');

exports.parentByPath = function () {

    var result = content.query({
        'count': 10,
        'parent': '/a/b'
    });

    assert.assertEquals(20, result.total);
    assert.assertEquals(3, result.count);
};

exports.parentById = function () {

    var result = content.query({
        'count': 10,
        'parent': '123456',
        'sort': '_manualordervalue DESC'
    });

    assert.assertEquals(20, result.total);
    assert.assertEquals(3, result.count);
};
