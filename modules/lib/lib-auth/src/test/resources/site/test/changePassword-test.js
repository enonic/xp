var auth = require('/lib/xp/auth.js');

exports.changePassword = function () {
    auth.changePassword({
        userKey: "user:myUserStore:userId",
        password: " test-password-   without- spaces  "
    });
};
