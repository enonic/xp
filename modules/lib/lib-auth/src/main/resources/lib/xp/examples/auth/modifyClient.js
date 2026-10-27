var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Switches a client off; its tokens run out and it cannot refresh.
var client = authLib.modifyClient({
    idProvider: 'system',
    id: 'claude',
    enabled: false
});
// END

t.assertEquals(false, client.enabled);
t.assertEquals('Claude', client.displayName);
