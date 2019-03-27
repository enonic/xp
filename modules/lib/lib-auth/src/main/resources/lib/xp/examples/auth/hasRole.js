var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Checks if the user has the specified role.
var flag = authLib.hasRole('system.admin.login');

log.info('The user ' + (flag ? 'has' : 'does not have') +
         ' access to the admin console.');
// END

t.assertTrue(flag);
