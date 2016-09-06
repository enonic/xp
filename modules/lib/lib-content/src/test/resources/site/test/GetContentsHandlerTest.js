var assert = require('/lib/xp/assert.js');
var content = require('/lib/xp/content.js');

var expectedJson = [
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

var expectedPartialJson = [
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
    }
];

exports.getByIds = function () {
    var result = content.getContents({
        keys: ['123456', '654321']
    });

    assert.assertJsonEquals(expectedJson, result);
};

exports.getByPaths = function () {
    var result = content.getContents({
        keys: ['/a/b/mycontent', '/c/d/othercontent']
    });

    assert.assertJsonEquals(expectedJson, result);
};

exports.getByIds_notFound = function () {
    var result = content.getContents({
        keys: ['/a/b/mycontent', '654321', '123456']
    });

    assert.assertJsonEquals(expectedPartialJson, result);
};

exports.getByPaths_notFound = function () {
    var result = content.getContents({
        keys: ['123456', '/c/d/othercontent', '/a/b/mycontent']
    });

    assert.assertJsonEquals(expectedPartialJson, result);
};
