var assert = require('/lib/xp/testing.js');
var portal = require('/lib/xp/portal.js');

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
        "type": "page",
        "path": "/",
        "descriptor": "myapplication:mycontroller",
        "config": {
            "a": "1"
        },
        "regions": {
            "top": {
                "components": [
                    {
                        "path": "/top/0",
                        "type": "part",
                        "descriptor": "myapplication:mypart",
                        "config": {
                            "a": "1"
                        }
                    },
                    {
                        "path": "/top/1",
                        "type": "layout",
                        "descriptor": "myapplication:mylayout",
                        "config": {
                            "a": "1"
                        },
                        "regions": {
                            "bottom": {
                                "components": [
                                    {
                                        "path": "/top/1/bottom/0",
                                        "type": "part",
                                        "descriptor": "myapplication:mypart",
                                        "config": {
                                            "a": "1"
                                        }
                                    }
                                ]
                            }
                        }
                    }
                ]
            }
        }
    },
    "attachments": {},
    "publish": {}
};

exports.currentContent = function () {
    var result = portal.getContent();
    assert.assertJsonEquals(expectedJson, result);
};

exports.noCurrentContent = function () {
    var result = portal.getContent();
    assert.assertNull(result);
};
