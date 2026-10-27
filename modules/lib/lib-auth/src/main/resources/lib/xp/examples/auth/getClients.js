var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// The clients registered with the system ID provider, by id.
var clients = authLib.getClients({idProvider: 'system'});
// END

t.assertEquals(1, clients.length);
t.assertEquals('claude', clients[0].id);
