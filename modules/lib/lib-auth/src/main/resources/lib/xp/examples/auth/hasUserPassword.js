const authLib = require('/lib/xp/auth');
const t = require('/lib/xp/testing');

// BEGIN
// Checks if a user has a password.
const result = authLib.hasUserPassword('user:enonic:user1');
// END

t.assertFalse(result);
