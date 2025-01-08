const auth = require('/lib/xp/auth.js');
const t = require('/lib/xp/testing.js');

exports.hasUserPassword = function () {
    const result = auth.hasUserPassword('user:enonic:user1');
    t.assertTrue(result);
};
