var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing.js');

var TestClass = Java.type('com.enonic.xp.lib.content.CreateMediaHandlerTest');
var stream = TestClass.createByteSource('Hello World');

exports.updateMediaValidate = function () {
    try {
        contentLib.updateMedia({});
    } catch (e) {
        assert.assertEquals('Parameter \'data\' is required', e.message);
    }

    try {
        contentLib.updateMedia({
            data: stream,
        });
    } catch (e) {
        assert.assertEquals('Parameter \'key\' is required', e.message);
    }

    try {
        contentLib.updateMedia({
            data: stream,
            key: 123,
        });
    } catch (e) {
        assert.assertEquals(`Required parameter 'key' is not a string!`, e.message);
    }

    try {
        contentLib.updateMedia({
            data: stream,
            key: '/a/b/c',
        });
    } catch (e) {
        assert.assertEquals('Parameter \'name\' is required', e.message);
    }

    try {
        contentLib.updateMedia({
            data: stream,
            key: '/a/b/c',
            name: 'media',
            focalX: 'invalidValue'
        });
    } catch (e) {
        assert.assertEquals(`Optional parameter 'focalX' is not a number!`, e.message);
    }
};

exports.updateMediaContentNotFoundByPath = function () {
    assert.assertNull(contentLib.updateMedia({
        data: stream,
        key: '/a/b/c',
        name: 'media',
    }));
};

exports.updateMediaContentNotFoundById = function () {
    assert.assertNull(contentLib.updateMedia({
        data: stream,
        key: 'contentId',
        name: 'media',
    }));
};

exports.updateMediaThrowContentNotFound = function () {
    assert.assertNull(contentLib.updateMedia({
        data: stream,
        key: '/a/b/c',
        name: 'media',
    }));
};
