var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.noUserStore = function () {
    var result = auth.getIdProviderConfig();
    assert.assertNull('AuthConfig not null', result);
};
