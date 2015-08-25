var assert = Java.type('org.junit.Assert');
var scriptAssert = Java.type('com.enonic.xp.testing.script.ScriptAssert');
var portal = require('/lib/xp/portal.js');

var expectedJson = {
    "_id": "100123",
    "_name": "my-content",
    "_path": "/my-content",
    "data": {
        "siteConfig": {
            "config": {
                "Field": 42
            },
            "applicationKey": "myapplication"
        }
    },
    "hasChildren": false,
    "page": {},
    "type": "base:unstructured",
    "valid": false,
    "x": {}
};

exports.currentSite = function () {
    var result = portal.getSite();
    scriptAssert.assertJson(expectedJson, result);
};

exports.noCurrentSite = function () {
    var result = portal.getSite();
    assert.assertNull(result);
};
