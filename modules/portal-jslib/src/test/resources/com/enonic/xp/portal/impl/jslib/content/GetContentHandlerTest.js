var expectedJson = {
    "_id": "123456",
    "_name": "mycontent",
    "_path": "/a/b/mycontent",
    "createdTime": "1970-01-01T00:00:00Z",
    "creator": "user:system:admin",
    "data": {
        "binaryReference": "abc",
        "boolean": [true, true, false],
        "c": {
            "d": true,
            "e": ["3", "4", "5"],
            "f": 2
        },
        "double": 2.2,
        "doubles": [1.1, 2.2, 3.3],
        "geoPoint": "1.1,-1.1",
        "geoPoints": ["1.1,-1.1", "2.2,-2.2"],
        "htmlPart": "<p>some<b>html</b></p>",
        "instant": "+1000000000-12-31T23:59:59.999999999Z",
        "link": "/my/content",
        "localDate": "2014-01-31",
        "localDateTime": "2014-01-31T10:30:05",
        "long": 1,
        "longs": [1, 2, 3],
        "set": {
            "property": "value"
        },
        "string": "a",
        "stringEmpty": "",
        "strings": ["a", "b", "c"],
        "xml": "<xml><my-xml hello='world'/></xml>"
    },
    "displayName": "My Content",
    "hasChildren": false,
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
    "type": "base:unstructured",
    "valid": false,
    "x": {
        "com-enonic-mymodule": {
            "myschema": {
                "a": "1"
            }
        }
    }
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
