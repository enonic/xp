var assert = Java.type('org.junit.Assert');
var scriptAssert = Java.type('com.enonic.xp.testing.script.ScriptAssert');
var portal = require('/lib/xp/portal.js');

var expectedJson = {
    "Field": 42
};

exports.currentSite = function () {
    var result = portal.getSiteConfig();
    scriptAssert.assertJson(expectedJson, result);
};

exports.noCurrentSite = function () {
    var result = portal.getSiteConfig();
    assert.assertNull(result);
};
