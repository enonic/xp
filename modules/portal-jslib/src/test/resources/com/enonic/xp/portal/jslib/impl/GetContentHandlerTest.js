var expectedJson = {
    "_createdTime": "1970-01-01T00:00:00Z",
    "_creator": "user:system:admin",
    "_id": "123456",
    "_modifiedTime": "1970-01-01T00:00:00Z",
    "_modifier": "user:system:admin",
    "_name": "mycontent",
    "_path": "/a/b/mycontent",
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
    "draft": true,
    "hasChildren": false,
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

exports.getContent = function () {
    var result = execute('portal.getContent');

    assert.assertJson(expectedJson, result);
};

exports.getContent_notFound = function () {
    var result = execute('portal.getContent');

    assert.assertNull(result);
};
