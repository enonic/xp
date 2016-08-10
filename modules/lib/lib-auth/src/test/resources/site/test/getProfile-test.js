var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.noProfile = function () {
    var result = auth.getProfile({
        key: "user:enonic:user1",
        scope: "myapp"
    });
    //assert.assertNull('Profile not null', result);
    assert.assertJsonEquals({}, result);
};
