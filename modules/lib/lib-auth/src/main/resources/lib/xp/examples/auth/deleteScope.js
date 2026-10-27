var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Removes a scope from the system ID provider.
var deleted = authLib.deleteScope({idProvider: 'system', id: 'content:write'});
// END

t.assertTrue(deleted);
