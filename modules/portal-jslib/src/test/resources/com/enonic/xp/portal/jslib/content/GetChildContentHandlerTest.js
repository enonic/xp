var expectedJson = {
    "contents": [{
        "_createdTime": "1970-01-01T00:00:00Z",
        "_creator": "user:system:admin",
        "_id": "111111",
        "_modifiedTime": "1970-01-01T00:00:00Z",
        "_modifier": "user:system:admin",
        "_name": "mycontent",
        "_path": "/a/b/mycontent",
        "data": {},
        "displayName": "My Content",
        "draft": true,
        "hasChildren": false,
        "meta": {},
        "page": {},
        "type": "base:unstructured"
    }, {
        "_createdTime": "1970-01-01T00:00:00Z",
        "_creator": "user:system:admin",
        "_id": "222222",
        "_modifiedTime": "1970-01-01T00:00:00Z",
        "_modifier": "user:system:admin",
        "_name": "othercontent",
        "_path": "/a/b/othercontent",
        "data": {},
        "displayName": "Other Content",
        "draft": true,
        "hasChildren": false,
        "meta": {},
        "page": {},
        "type": "base:unstructured"
    }, {
        "_createdTime": "1970-01-01T00:00:00Z",
        "_creator": "user:system:admin",
        "_id": "333333",
        "_modifiedTime": "1970-01-01T00:00:00Z",
        "_modifier": "user:system:admin",
        "_name": "another",
        "_path": "/a/b/another",
        "data": {},
        "displayName": "Another Content",
        "draft": true,
        "hasChildren": false,
        "meta": {},
        "page": {},
        "type": "base:unstructured"
    }],
    "total": 20
};

var expectedEmptyJson = {
    "contents": [],
    "total": 0
};

exports.getChildrenById = function () {
    var result = execute('content.getChildren', {
        key: '123456'
    });

    assert.assertJson(expectedJson, result);
};

exports.getChildrenByPath = function () {
    var result = execute('content.getChildren', {
        key: '/a/b'
    });

    assert.assertJson(expectedJson, result);
};

exports.getChildrenById_notFound = function () {
    var result = execute('content.getChildren', {
        key: '123456'
    });

    assert.assertJson(expectedEmptyJson, result);
};

exports.getChildrenByPath_notFound = function () {
    var result = execute('content.getChildren', {
        key: '/a/b/mycontent'
    });

    assert.assertJson(expectedEmptyJson, result);
};

exports.getChildrenByPath_allParameters = function () {
    var result = execute('content.getChildren', {
        key: '/a/b/mycontent',
        start: 5,
        count: 3,
        sort: '_modifiedTime ASC'
    });

    assert.assertJson(expectedJson, result);
};
