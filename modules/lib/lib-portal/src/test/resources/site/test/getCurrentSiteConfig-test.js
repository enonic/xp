var assert = require('/lib/xp/assert.js');
var portal = require('/lib/xp/portal.js');

var expectedJson = {
    "Field": 42
};

exports.currentSite = function () {
    var result = portal.getSiteConfig();
    assert.assertJsonEquals('SiteConfig JSON not equals', expectedJson, result);
};

exports.noCurrentSite = function () {
    var result = portal.getSiteConfig();
    assert.assertNull('SiteConfig not null', result);
};
