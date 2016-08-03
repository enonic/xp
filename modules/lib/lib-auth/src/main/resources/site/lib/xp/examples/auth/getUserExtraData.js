var authLib = require('/lib/xp/auth');
var assert = require('/lib/xp/assert');

// BEGIN
// Returns the current loggedin user.
var userExtraData = authLib.getUserExtraData({
    key: "user:enonic:user1",
    namespace: "com.enonic.app.myapp"
});
// END

// BEGIN
// Information when retrieving a user.
var expected = {
    "set": {
        "subString": "subStringValue",
        "subLong": 123
    },
    "string": "stringValue"
};
// END

assert.assertJsonEquals(expected, userExtraData);
