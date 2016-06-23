var assert = require('/lib/xp/assert.js');
var portal = require('/lib/xp/portal.js');

exports.noUserStore = function () {
    var result = portal.getUserStoreKey();
    assert.assertEquals('getUserStore result not null', null, result);
};