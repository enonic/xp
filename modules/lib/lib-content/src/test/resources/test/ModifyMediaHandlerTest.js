var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing.js');

var TestClass = Java.type('com.enonic.xp.lib.content.CreateMediaHandlerTest');
var stream = TestClass.createByteSource('Hello World');

exports.modifyMediaValidate = function () {
    try {
        contentLib.modifyMedia({});
    } catch (e) {
        assert.assertEquals('Parameter \'data\' is required', e);
    }

    try {
        contentLib.modifyMedia({
            data: stream,
        });
    } catch (e) {
        assert.assertEquals('Parameter \'key\' is required', e);
    }

    try {
        contentLib.modifyMedia({
            data: stream,
            key: 123,
        });
    } catch (e) {
        assert.assertEquals(`Required parameter 'key' is not a string!`, e);
    }

    try {
        contentLib.modifyMedia({
            data: stream,
            key: '/a/b/c',
        });
    } catch (e) {
        assert.assertEquals('Parameter \'name\' is required', e);
    }

    try {
        contentLib.modifyMedia({
            data: stream,
            key: '/a/b/c',
            name: 'media',
            focalX: 'invalidValue'
        });
    } catch (e) {
        assert.assertEquals(`Optional parameter 'focalX' is not a number!`, e);
    }
};

exports.modifyMediaContentNotFoundByPath = function () {
    assert.assertNull(contentLib.modifyMedia({
        data: stream,
        key: '/a/b/c',
        name: 'media',
    }));
};

exports.modifyMediaContentNotFoundById = function () {
    assert.assertNull(contentLib.modifyMedia({
        data: stream,
        key: 'contentId',
        name: 'media',
    }));
};

exports.modifyMediaThrowContentNotFound = function () {
    assert.assertNull(contentLib.modifyMedia({
        data: stream,
        key: '/a/b/c',
        name: 'media',
    }));
};
