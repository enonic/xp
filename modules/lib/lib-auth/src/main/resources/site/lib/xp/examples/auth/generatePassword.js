var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Generate a password and returns the password string.
var pwd = authLib.generatePassword();
log.info('New password: %s', pwd);
// END

t.assertNotNull(pwd);
