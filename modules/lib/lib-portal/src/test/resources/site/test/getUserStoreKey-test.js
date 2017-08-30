var assert = require('/lib/xp/testing.js');
var portal = require('/lib/xp/portal.js');

exports.noUserStore = function () {
    var result = portal.getUserStoreKey();
    assert.assertEquals(null, result);
};