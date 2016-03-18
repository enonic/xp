var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.noUserStore = function () {
    var result = auth.getUser();
    assert.assertEquals('getUserStore result not null', null, result);
};