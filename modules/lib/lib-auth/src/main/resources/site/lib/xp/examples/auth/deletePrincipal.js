var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Returns the principal information for specified principal key.
var deleted = authLib.deletePrincipal('user:myUserStore:userId');
// END

// BEGIN
// Information when getting a principal.
var expected = true;
// END

t.assertJsonEquals(expected, deleted);
