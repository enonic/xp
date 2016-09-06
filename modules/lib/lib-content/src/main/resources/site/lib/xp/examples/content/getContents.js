var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/assert');

// BEGIN
// Gets a single content by path.
var result = contentLib.getContents({
    keys: ['/a/b/mycontent', '123456', '654321'],
    branch: 'draft'
});

if (result) {
    log.info('Results length = ' + result.length);
} else {
    log.info('Content was not found');
}
// END

// BEGIN
// Content as it is returned.
var expected = [
    {
        "_id": "id1",
        "_name": "name1",
        "_path": "/a/b/name1",
        "creator": "user:system:admin",
        "modifier": "user:system:admin",
        "createdTime": "1970-01-01T00:00:00Z",
        "modifiedTime": "1970-01-01T00:00:00Z",
        "type": "base:unstructured",
        "displayName": "My Content 1",
        "hasChildren": false,
        "valid": false,
        "data": {},
        "x": {},
        "page": {},
        "attachments": {}
    },
    {
        "_id": "id1",
        "_name": "name1",
        "_path": "/a/b/name1",
        "creator": "user:system:admin",
        "modifier": "user:system:admin",
        "createdTime": "1970-01-01T00:00:00Z",
        "modifiedTime": "1970-01-01T00:00:00Z",
        "type": "base:unstructured",
        "displayName": "My Content 1",
        "hasChildren": false,
        "valid": false,
        "data": {},
        "x": {},
        "page": {},
        "attachments": {}
    },
    {
        "_id": "id2",
        "_name": "name2",
        "_path": "/a/b/name2",
        "creator": "user:system:admin",
        "modifier": "user:system:admin",
        "createdTime": "1970-01-01T00:00:00Z",
        "modifiedTime": "1970-01-01T00:00:00Z",
        "type": "base:unstructured",
        "displayName": "My Content 2",
        "hasChildren": false,
        "valid": false,
        "data": {},
        "x": {},
        "page": {},
        "attachments": {}
    }
];
// END

assert.assertJsonEquals(expected, result);
