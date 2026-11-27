var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Checks if the request may change orders. A request without a token is not limited by scopes.
var flag = authLib.hasScope('com.example.orders:write');

log.info('The request ' + (flag ? 'may' : 'may not') + ' change orders.');
// END

t.assertTrue(flag);
