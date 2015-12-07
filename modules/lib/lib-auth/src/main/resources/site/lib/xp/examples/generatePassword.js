var authLib = require('/lib/xp/auth');
var assert = require('/lib/xp/assert');

// BEGIN
// Generate a password and returns the password string.
var pwd = authLib.generatePassword();
log.info('New password: %s', pwd);
// END

assert.assertNotNull(pwd);
