var assert = require('/lib/xp/assert.js');
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
    "page": {},
    "attachments": {}
};

exports.currentSite = function () {
    var result = portal.getSite();
    assert.assertJsonEquals('Site JSON not equals', expectedJson, result);
};

exports.noCurrentSite = function () {
    var result = portal.getSite();
    assert.assertNull('Site JSON not null', result);
};
