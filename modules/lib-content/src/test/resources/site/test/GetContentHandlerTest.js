var assert = Java.type('org.junit.Assert');
var content = require('/lib/xp/content.js');

var expectedJson = {
    "_id": "123456",
    "_name": "mycontent",
    "_path": "/a/b/mycontent",
    "creator": "user:system:admin",
    "modifier": "user:system:admin",
    "createdTime": "1970-01-01T00:00:00Z",
    "modifiedTime": "1970-01-01T00:00:00Z",
    "type": "base:unstructured",
    "displayName": "My Content",
    "hasChildren": false,
    "language": "en",
    "valid": false,
    "data": {
        "boolean": [
            true,
            true,
            false
        ],
        "long": 1,
        "longs": [
            1,
            2,
            3
        ],
        "double": 2.2,
        "doubles": [
            1.1,
            2.2,
            3.3
        ],
        "string": "a",
        "strings": [
            "a",
            "b",
            "c"
        ],
        "stringEmpty": "",
        "set": {
            "property": "value"
        },
        "xml": "<xml><my-xml hello='world'/></xml>",
        "binaryReference": "abc",
        "link": "/my/content",
        "geoPoint": "1.1,-1.1",
        "geoPoints": [
            "1.1,-1.1",
            "2.2,-2.2"
        ],
        "instant": "+1000000000-12-31T23:59:59.999999999Z",
        "localDate": "2014-01-31",
        "localDateTime": "2014-01-31T10:30:05",
        "c": {
            "d": true,
            "e": [
                "3",
                "4",
                "5"
            ],
            "f": 2
        }
    },
    "x": {
        "com-enonic-myapplication": {
            "myschema": {
                "a": "1"
            }
        }
    },
    "page": {
        "controller": "myapplication:mycontroller",
        "config": {
            "a": "1"
        },
        "regions": {
            "top": {
                "components": [
                    {
                        "name": "mypart",
                        "path": "top/0",
                        "type": "part",
                        "descriptor": "myapplication:mypart",
                        "config": {
                            "a": "1"
                        }
                    },
                    {
                        "name": "mylayout",
                        "path": "top/1",
                        "type": "layout",
                        "descriptor": "myapplication:mylayout",
                        "config": {
                            "a": "1"
                        },
                        "regions": {
                            "bottom": {
                                "components": [
                                    {
                                        "name": "mypart",
                                        "path": "top/1/bottom/0",
                                        "type": "part",
                                        "descriptor": "myapplication:mypart",
                                        "config": {
                                            "a": "1"
                                        }
                                    }
                                ],
                                "name": "bottom"
                            }
                        }
                    }
                ],
                "name": "top"
            }
        }
    }
};

function assertJson(expected, result) {
    assert.assertEquals(JSON.stringify(expected, null, 2), JSON.stringify(result, null, 2));
}

exports.getById = function () {
    var result = content.get({
        key: '123456'
    });

    assertJson(expectedJson, result);
};

exports.getByPath = function () {
    var result = content.get({
        key: '/a/b/mycontent'
    });

    assertJson(expectedJson, result);
};

exports.getById_notFound = function () {
    var result = content.get({
        key: '123456'
    });

    assert.assertNull(result);
};

exports.getByPath_notFound = function () {
    var result = content.get({
        key: '/a/b/mycontent'
    });

    assert.assertNull(result);
};
