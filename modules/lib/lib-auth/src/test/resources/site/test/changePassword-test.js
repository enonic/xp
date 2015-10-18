var auth = require('/lib/xp/auth.js');

exports.changePassword = function () {
    auth.changePassword("test-password");
};