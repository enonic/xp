var contentLib = require('/lib/xp/content');
var t = require('/lib/xp/testing');

var TestClass = Java.type('com.enonic.xp.lib.content.CreateMediaHandlerTest');
var stream = TestClass.createByteSource('Hello World');

// BEGIN
// Update media
var updateResult = contentLib.updateMedia({
    key: '/a/b/myMedia',
    name: 'myMedia',
    data: stream,
    artist: ['Artist 1', 'Artist 2'],
    caption: 'Caption',
    copyright: 'Copyright',
    mimeType: 'text/plan',
    tags: ['tag1', 'tag2'],
    workflow: {
        state: 'IN_PROGRESS',
        checks: {}
    }
});
// END

// BEGIN
// Content modified.
var expectedJson = {
    "_id": "123456",
    "_name": "myMedia",
    "_path": "/a/b/myMedia",
    "creator": "user:system:anonymous",
    "createdTime": "1975-01-08T00:00:00Z",
    "type": "base:unstructured",
    "valid": false,
    "data": {
        "caption": "Caption",
        "artist": [
            "Artist 1",
            "Artist 2"
        ],
        "copyright": "Copyright",
        "mimeType": "text/plan",
        "tags": [
            "tag1",
            "tag2"
        ]
    },
    "x": {},
    "page": {},
    "attachments": {},
    "publish": {},
    "workflow": {
        "state": "IN_PROGRESS",
        "checks": {}
    }
};
// END

t.assertJsonEquals(expectedJson, updateResult);
