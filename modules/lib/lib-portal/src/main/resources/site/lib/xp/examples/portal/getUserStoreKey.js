var authLib = require('/lib/xp/auth');
var assert = require('/lib/xp/assert');

// BEGIN
// Returns the current user store.
var userStoreKey = authLib.getUserStoreKey();

if (userStoreKey) {
    log.info('User store key: %s', userStoreKey);
}
// END

// BEGIN
// Information when retrieving a user.
var expected = "system";
// END

assert.assertJsonEquals(expected, userStore);
