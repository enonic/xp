var expectedJson = {
    "_id": "123456",
    "_name": "mycontent",
    "_path": "/a/b/mycontent",
    "createdTime": "1970-01-01T00:00:00Z",
    "creator": "user:system:admin",
    "data": {
        "a": 1,
        "b": "2",
        "c": [{
            "d": true
        }, {
            "d": true,
            "e": ["3", "4", "5"],
            "f": 2
        }]
    },
    "displayName": "My Content",
    "draft": true,
    "hasChildren": false,
    "meta": {
        "myschema": {
            "a": "1"
        }
    },
    "modifiedTime": "1970-01-01T00:00:00Z",
    "modifier": "user:system:admin",
    "page": {
        "config": {
            "a": "1"
        },
        "controller": "mymodule:mycontroller",
        "regions": {
            "top": {
                "components": [{
                    "config": {
                        "a": "1"
                    },
                    "descriptor": "mymodule:mypart",
                    "name": "mypart",
                    "path": "top/0",
                    "type": "part"
                }, {
                    "config": {
                        "a": "1"
                    },
                    "descriptor": "mymodule:mylayout",
                    "name": "mylayout",
                    "path": "top/1",
                    "regions": {
                        "bottom": {
                            "components": [{
                                "config": {
                                    "a": "1"
                                },
                                "descriptor": "mymodule:mypart",
                                "name": "mypart",
                                "path": "top/1/bottom/0",
                                "type": "part"
                            }]
                        }
                    },
                    "type": "layout"
                }]
            }
        }
    },
    "type": "base:unstructured"
};

exports.getById = function () {
    var result = execute('content.get', {
        key: '123456'
    });

    assert.assertJson(expectedJson, result);
};

exports.getByPath = function () {
    var result = execute('content.get', {
        key: '/a/b/mycontent'
    });

    assert.assertJson(expectedJson, result);
};

exports.getById_notFound = function () {
    var result = execute('content.get', {
        key: '123456'
    });

    assert.assertNull(result);
};

exports.getByPath_notFound = function () {
    var result = execute('content.get', {
        key: '/a/b/mycontent'
    });

    assert.assertNull(result);
};
