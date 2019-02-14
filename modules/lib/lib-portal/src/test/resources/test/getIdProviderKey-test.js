var assert = require('/lib/xp/testing.js');
var portal = require('/lib/xp/portal.js');

exports.noIdProvider = function () {
    var result = portal.getIdProviderKey();
    assert.assertEquals(null, result);
};