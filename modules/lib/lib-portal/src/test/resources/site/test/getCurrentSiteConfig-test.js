var assert = require('/lib/xp/testing.js');
var portal = require('/lib/xp/portal.js');

var expectedJson = {
    "Field": 42
};

exports.currentSite = function () {
    var result = portal.getSiteConfig();
    assert.assertJsonEquals(expectedJson, result);
};

exports.noCurrentSite = function () {
    var result = portal.getSiteConfig();
    assert.assertNull(result);
};
