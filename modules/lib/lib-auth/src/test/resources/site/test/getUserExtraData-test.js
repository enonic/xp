var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.noExtraData = function () {
    var result = auth.getUserExtraData({
        key: "user:enonic:user1",
        namespace: "com.enonic.app.myapp"
    });
    assert.assertNull('ExtraData not null', result);
};
