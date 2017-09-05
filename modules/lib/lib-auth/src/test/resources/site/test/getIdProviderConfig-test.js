var t = require('/lib/xp/testing.js');
var auth = require('/lib/xp/auth.js');

exports.noUserStore = function () {
    var result = auth.getIdProviderConfig();
    t.assertNull(result);
};
