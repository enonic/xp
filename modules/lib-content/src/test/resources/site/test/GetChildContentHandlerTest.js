var assert = Java.type('org.junit.Assert');
var content = require('/lib/xp/content.js');

var expectedJson = {
    "total": 20,
    "count": 3,
    "hits": [
        {
            "_id": "111111",
            "_name": "mycontent",
            "_path": "/a/b/mycontent",
            "creator": "user:system:admin",
            "modifier": "user:system:admin",
            "createdTime": "1970-01-01T00:00:00Z",
            "modifiedTime": "1970-01-01T00:00:00Z",
            "type": "base:unstructured",
            "displayName": "My Content",
            "hasChildren": false,
            "valid": false,
            "data": {},
            "x": {},
            "page": {}
        },
        {
            "_id": "222222",
            "_name": "othercontent",
            "_path": "/a/b/othercontent",
            "creator": "user:system:admin",
            "modifier": "user:system:admin",
            "createdTime": "1970-01-01T00:00:00Z",
            "modifiedTime": "1970-01-01T00:00:00Z",
            "type": "base:unstructured",
            "displayName": "Other Content",
            "hasChildren": false,
            "valid": false,
            "data": {},
            "x": {},
            "page": {}
        },
        {
            "_id": "333333",
            "_name": "another",
            "_path": "/a/b/another",
            "creator": "user:system:admin",
            "modifier": "user:system:admin",
            "createdTime": "1970-01-01T00:00:00Z",
            "modifiedTime": "1970-01-01T00:00:00Z",
            "type": "base:unstructured",
            "displayName": "Another Content",
            "hasChildren": false,
            "valid": false,
            "data": {},
            "x": {},
            "page": {}
        }
    ]
};

var expectedEmptyJson = {
    "total": 0,
    "count": 0,
    "hits": []
};

function assertJson(expected, result) {
    assert.assertEquals(JSON.stringify(expected, null, 2), JSON.stringify(result, null, 2));
}

exports.getChildrenById = function () {
    var result = content.getChildren({
        key: '123456'
    });

    assertJson(expectedJson, result);
};

exports.getChildrenByPath = function () {
    var result = content.getChildren({
        key: '/a/b'
    });

    assertJson(expectedJson, result);
};

exports.getChildrenById_notFound = function () {
    var result = content.getChildren({
        key: '123456'
    });

    assertJson(expectedEmptyJson, result);
};

exports.getChildrenByPath_notFound = function () {
    var result = content.getChildren({
        key: '/a/b/mycontent'
    });

    assertJson(expectedEmptyJson, result);
};

exports.getChildrenByPath_allParameters = function () {
    var result = content.getChildren({
        key: '/a/b/mycontent',
        start: 5,
        count: 3,
        sort: '_modifiedTime ASC'
    });

    assertJson(expectedJson, result);
};
