var assert = require('/lib/xp/assert.js');
var content = require('/lib/xp/content.js');

var TestClass = Java.type('com.enonic.xp.lib.content.CreateMediaHandlerTest');

var expectedJson = {
    "_id": "123456",
    "_name": "mycontent",
    "_path": "/a/b/mycontent",
    "creator": "user:system:anonymous",
    "createdTime": "1975-01-08T00:00:00Z",
    "type": "base:unstructured",
    "hasChildren": false,
    "valid": false,
    "data": {},
    "x": {},
    "page": {}
};

exports.createMedia = function () {
    var result = content.createMedia({
        name: 'mycontent',
        parentPath: '/a/b',
        mimeType: 'text/plain',
        data: TestClass.createByteSource('Hello World')
    });

    assert.assertJsonEquals(expectedJson, result);
};

