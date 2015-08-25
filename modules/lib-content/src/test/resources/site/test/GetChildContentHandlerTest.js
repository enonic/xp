var scriptAssert = Java.type('com.enonic.xp.testing.script.ScriptAssert');
var content = require('/lib/xp/content.js');

var expectedJson = {
    "count": 3,
    "hits": [{
        "_id": "111111",
        "_name": "mycontent",
        "_path": "/a/b/mycontent",
        "createdTime": "1970-01-01T00:00:00Z",
        "creator": "user:system:admin",
        "data": {},
        "displayName": "My Content",
        "hasChildren": false,
        "modifiedTime": "1970-01-01T00:00:00Z",
        "modifier": "user:system:admin",
        "page": {},
        "type": "base:unstructured",
        "valid": false,
        "x": {}
    }, {
        "_id": "222222",
        "_name": "othercontent",
        "_path": "/a/b/othercontent",
        "createdTime": "1970-01-01T00:00:00Z",
        "creator": "user:system:admin",
        "data": {},
        "displayName": "Other Content",
        "hasChildren": false,
        "modifiedTime": "1970-01-01T00:00:00Z",
        "modifier": "user:system:admin",
        "page": {},
        "type": "base:unstructured",
        "valid": false,
        "x": {}
    }, {
        "_id": "333333",
        "_name": "another",
        "_path": "/a/b/another",
        "createdTime": "1970-01-01T00:00:00Z",
        "creator": "user:system:admin",
        "data": {},
        "displayName": "Another Content",
        "hasChildren": false,
        "modifiedTime": "1970-01-01T00:00:00Z",
        "modifier": "user:system:admin",
        "page": {},
        "type": "base:unstructured",
        "valid": false,
        "x": {}
    }],
    "total": 20
};

var expectedEmptyJson = {
    "count": 0,
    "hits": [],
    "total": 0
};

exports.getChildrenById = function () {
    var result = content.getChildren({
        key: '123456'
    });

    scriptAssert.assertJson(expectedJson, result);
};

exports.getChildrenByPath = function () {
    var result = content.getChildren({
        key: '/a/b'
    });

    scriptAssert.assertJson(expectedJson, result);
};

exports.getChildrenById_notFound = function () {
    var result = content.getChildren({
        key: '123456'
    });

    scriptAssert.assertJson(expectedEmptyJson, result);
};

exports.getChildrenByPath_notFound = function () {
    var result = content.getChildren({
        key: '/a/b/mycontent'
    });

    scriptAssert.assertJson(expectedEmptyJson, result);
};

exports.getChildrenByPath_allParameters = function () {
    var result = content.getChildren({
        key: '/a/b/mycontent',
        start: 5,
        count: 3,
        sort: '_modifiedTime ASC'
    });

    scriptAssert.assertJson(expectedJson, result);
};
