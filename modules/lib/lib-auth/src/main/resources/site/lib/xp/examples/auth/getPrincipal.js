var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Returns the principal information for specified principal key.
var principal = authLib.getPrincipal('user:myUserStore:userId');
// END

// BEGIN
// Information when getting a principal.
var expected = {
    "type": "user",
    "key": "user:enonic:user1",
    "displayName": "User 1",
    "modifiedTime": "1970-01-01T00:00:00Z",
    "disabled": false,
    "email": "user1@enonic.com",
    "login": "user1",
    "userStore": "enonic"
};
// END

t.assertJsonEquals(expected, principal);
