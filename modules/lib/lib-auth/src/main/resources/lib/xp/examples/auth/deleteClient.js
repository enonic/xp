var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Removes a client from the system ID provider.
var deleted = authLib.deleteClient({idProvider: 'system', id: 'claude'});
// END

t.assertTrue(deleted);
