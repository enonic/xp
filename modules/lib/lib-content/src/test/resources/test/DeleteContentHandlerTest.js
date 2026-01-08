const assert = require('/lib/xp/testing.js');
const contentLib = require('/lib/xp/content.js');

exports.deleteById = function () {
    let result = contentLib.deleteContent({
        key: '123456'
    });

    assert.assertEquals(true, result);
};

exports.deleteByPath = function () {
    let result = contentLib.deleteContent({
        key: '/a/b'
    });

    assert.assertEquals(true, result);
};

exports.deleteById_notFound = function () {
    let result = contentLib.deleteContent({
        key: '123456'
    });

    assert.assertEquals(false, result);
};

exports.deleteByPath_notFound = function () {
    let result = contentLib.deleteContent({
        key: '/a/b'
    });

    assert.assertEquals(false, result);
};
