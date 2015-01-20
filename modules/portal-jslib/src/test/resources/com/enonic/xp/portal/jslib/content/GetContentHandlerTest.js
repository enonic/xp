var expectedJson = {
    "_createdTime": "1970-01-01T00:00:00Z",
    "_creator": "user:system:admin",
    "_id": "123456",
    "_modifiedTime": "1970-01-01T00:00:00Z",
    "_modifier": "user:system:admin",
    "_name": "mycontent",
    "_path": "/a/b/mycontent",
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
    "draft": false,
    "hasChildren": false,
    "isPageTemplate": false,
    "isSite": false,
    "meta": {
        "mymodule:myschema": {
            "a": "1"
        }
    },
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
    "type": "system:unstructured"
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
