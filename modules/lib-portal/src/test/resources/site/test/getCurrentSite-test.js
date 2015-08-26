var assert = Java.type('org.junit.Assert');
var portal = require('/lib/xp/portal.js');

var expectedJson = {
    "_id": "100123",
    "_name": "my-content",
    "_path": "/my-content",
    "type": "base:unstructured",
    "hasChildren": false,
    "valid": false,
    "data": {
        "siteConfig": {
            "applicationKey": "myapplication",
            "config": {
                "Field": 42
            }
        }
    },
    "x": {},
    "page": {}
};

function assertJson(expected, result) {
    assert.assertEquals(JSON.stringify(expected, null, 2), JSON.stringify(result, null, 2));
}

exports.currentSite = function () {
    var result = portal.getSite();
    assertJson(expectedJson, result);
};

exports.noCurrentSite = function () {
    var result = portal.getSite();
    assert.assertNull(result);
};
