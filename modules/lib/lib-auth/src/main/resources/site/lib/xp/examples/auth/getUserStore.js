var authLib = require('/lib/xp/auth');
var assert = require('/lib/xp/assert');

// BEGIN
// Returns the current loggedin user.
var userStore = authLib.getUserStore();

if (userStore) {
    log.info('User store key: %s', userStore.key);
}
// END

// BEGIN
// Information when retrieving a user.
var expected = {
    "key": "userStoreTestKey",
    "displayName": "User store test",
    "description": "User store used for testing"
};
// END

assert.assertJsonEquals(expected, userStore);
