var authLib = require('/lib/xp/auth');

// BEGIN
// Find current logged-in user.
var user = authLib.getUser();

// Change password for the user.
authLib.changePassword({
    userKey: user.key,
    password: 'new-secret-password'
});
// END
