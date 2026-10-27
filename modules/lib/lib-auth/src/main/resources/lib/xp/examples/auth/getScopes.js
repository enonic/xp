var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// The scopes of the system ID provider, by id.
var scopes = authLib.getScopes({idProvider: 'system'});
// END

t.assertEquals(1, scopes.length);
t.assertEquals('content:write', scopes[0].id);
