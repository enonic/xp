var assert = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');

var expectedJson = {
    "total": 20,
    "count": 2,
    "hits": [
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
            "attachments": {},
            "publish": {}
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
            "attachments": {},
            "publish": {}
        }
    ]
};

exports.query = function () {

    var result = content.query({
            "start": 0,
            "count": 100,
            "filters": [
                {
                    "exists": {
                        "field": "field1"
                    }
                },
                {
                    "exists": {
                        "field": "field2"
                    }
                }
            ],
            "contentTypes": [
                "article",
                "comment"
            ]
        }
    );

    assert.assertJsonEquals(expectedJson, result);
};

