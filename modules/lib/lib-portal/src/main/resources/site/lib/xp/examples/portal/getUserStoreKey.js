var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
// Returns the current user store.
var userStoreKey = portalLib.getUserStoreKey();

if (userStoreKey) {
    log.info('User store key: %s', userStoreKey);
}
// END

// BEGIN
// User store key returned.
var expected = "myuserstore";
// END

assert.assertJsonEquals(expected, userStoreKey);
