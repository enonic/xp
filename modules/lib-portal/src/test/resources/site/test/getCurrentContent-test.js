var assert = Java.type('org.junit.Assert');
var scriptAssert = Java.type('com.enonic.xp.testing.script.ScriptAssert');
var portal = require('/lib/xp/portal.js');

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
    "language": "en",
    "modifiedTime": "1970-01-01T00:00:00Z",
    "modifier": "user:system:admin",
    "page": {
        "config": {
            "a": "1"
        },
        "controller": "myapplication:mycontroller",
        "regions": {
            "top": {
                "components": [{
                    "config": {
                        "a": "1"
                    },
                    "descriptor": "myapplication:mypart",
                    "name": "mypart",
                    "path": "top/0",
                    "type": "part"
                }, {
                    "config": {
                        "a": "1"
                    },
                    "descriptor": "myapplication:mylayout",
                    "name": "mylayout",
                    "path": "top/1",
                    "regions": {
                        "bottom": {
                            "components": [{
                                "config": {
                                    "a": "1"
                                },
                                "descriptor": "myapplication:mypart",
                                "name": "mypart",
                                "path": "top/1/bottom/0",
                                "type": "part"
                            }],
                            "name": "bottom"
                        }
                    },
                    "type": "layout"
                }],
                "name": "top"
            }
        }
    },
    "type": "base:unstructured",
    "valid": false,
    "x": {
        "com-enonic-myapplication": {
            "myschema": {
                "a": "1"
            }
        }
    }
};

exports.currentContent = function () {
    var result = portal.getContent();
    scriptAssert.assertJson(expectedJson, result);
};

exports.noCurrentContent = function () {
    var result = portal.getContent();
    assert.assertNull(result);
};
