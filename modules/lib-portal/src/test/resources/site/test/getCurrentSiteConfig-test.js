var assert = Java.type('org.junit.Assert');
var portal = require('/lib/xp/portal.js');

var expectedJson = {
    "Field": 42
};

function assertJson(expected, result) {
    assert.assertEquals(JSON.stringify(expected, null, 2), JSON.stringify(result, null, 2));
}

exports.currentSite = function () {
    var result = portal.getSiteConfig();
    assertJson(expectedJson, result);
};

exports.noCurrentSite = function () {
    var result = portal.getSiteConfig();
    assert.assertNull(result);
};
