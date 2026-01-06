var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Update content metadata by path
var result = contentLib.updateMetadata({
    key: '/a/b/mycontent',
    language: 'en',
    owner: 'user:system:new-owner'
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
    "contentId": "123456",
    "results": [
        {
            "branch": "draft",
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
                "data": {
                    "myfield": "Hello World"
                },
                "x": {},
                "page": {},
                "attachments": {},
                "publish": {}
            }
        }
    ]
};
// END

assert.assertJsonEquals(expected, result);
