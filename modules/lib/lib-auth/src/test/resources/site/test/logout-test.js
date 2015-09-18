var auth = require('/lib/xp/auth.js');

exports.logout = function () {

    auth.logout();

};

exports.alreadyLoggedOut = function () {

    auth.logout();

};