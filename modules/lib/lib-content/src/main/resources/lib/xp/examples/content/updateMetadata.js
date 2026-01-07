var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Update content metadata by path
var result = contentLib.updateMetadata({
    key: '/a/b/mycontent',
    editor: function (c) {
        c.language = 'en';
        c.owner = 'user:system:new-owner';
        return c;
    }
});

if (result) {
    log.info('Content metadata updated');
} else {
    log.info('Content not found');
}
// END

// BEGIN
// Content metadata updated.
var expected = {
    "content": {
        "_id": "123456",
        "_name": "mycontent",
        "_path": "/path/to/mycontent",
        "creator": "user:system:admin",
        "modifier": "user:system:admin",
        "createdTime": "1970-01-01T00:00:00Z",
        "modifiedTime": "1970-01-01T00:00:00Z",
        "owner": "user:system:new-owner",
        "type": "base:unstructured",
        "displayName": "My Content",
        "language": "en",
        "valid": true,
        "childOrder": "_ts DESC, _name ASC",
        "data": {
            "myfield": "Hello World"
        },
        "x": {},
        "page": {},
        "attachments": {
            "logo.png": {
                "name": "logo.png",
                "label": "small",
                "size": 6789,
                "mimeType": "image/png"
            },
            "document.pdf": {
                "name": "document.pdf",
                "size": 12345,
                "mimeType": "application/pdf"
            }
        },
        "publish": {},
        "workflow": {
            "state": "READY",
            "checks": {}
        }
    }
};
// END

assert.assertJsonEquals(expected, result);
