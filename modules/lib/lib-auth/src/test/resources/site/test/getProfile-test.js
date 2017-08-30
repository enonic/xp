var t = require('/lib/xp/testing.js');
var auth = require('/lib/xp/auth.js');

exports.noProfile = function () {
    var result = auth.getProfile({
        key: "user:enonic:user1",
        scope: "myapp"
    });
    t.assertNull(result);
};
