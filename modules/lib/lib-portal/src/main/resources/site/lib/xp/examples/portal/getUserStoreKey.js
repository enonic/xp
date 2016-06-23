var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/assert');

// BEGIN
// Returns the current user store.
var userStoreKey = portalLib.getUserStoreKey();

if (userStoreKey) {
    log.info('User store key: %s', userStoreKey);
}
// END

// BEGIN
// Information when retrieving a user.
var expected = "myuserstore";
// END

assert.assertJsonEquals(expected, userStoreKey);
