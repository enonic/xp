var assert = Java.type('org.junit.Assert');
var content = require('/lib/xp/content.js');

exports.deleteById = function () {
    var result = content.delete({
        key: '123456'
    });

    assert.assertEquals(true, result);
};

exports.deleteByPath = function () {
    var result = content.delete({
        key: '/a/b'
    });

    assert.assertEquals(true, result);
};

exports.deleteById_notFound = function () {
    var result = content.delete({
        key: '123456'
    });

    assert.assertEquals(false, result);
};

exports.deleteByPath_notFound = function () {
    var result = content.delete({
        key: '/a/b'
    });

    assert.assertEquals(false, result);
};
