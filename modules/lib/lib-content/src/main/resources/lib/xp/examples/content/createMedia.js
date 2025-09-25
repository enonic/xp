var contentLib = require('/lib/xp/content');
var t = require('/lib/xp/testing');

var TestClass = Java.type('com.enonic.xp.lib.content.CreateMediaHandlerTest');
var stream = TestClass.createByteSource('Hello World');

// BEGIN
// Creates a media.
var result = contentLib.createMedia({
    name: 'mycontent',
    parentPath: '/a/b',
    mimeType: 'text/plain',
    data: stream
});
// END

// BEGIN
// Content created.
var expected = {
    '_id': '123456',
    '_name': 'mycontent',
    '_path': '/a/b/mycontent',
    'creator': 'user:system:anonymous',
    'createdTime': '1975-01-08T00:00:00Z',
    'type': 'base:unstructured',
    'valid': false,
    'data': {},
    'x': {},
    'page': {},
    'attachments': {},
    'publish': {},
    'workflow': {
        'state': 'READY',
        'checks': {}
    }
};
// END

t.assertJsonEquals(expected, result);
