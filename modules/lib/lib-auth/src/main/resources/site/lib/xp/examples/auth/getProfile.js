var authLib = require('/lib/xp/auth');
var assert = require('/lib/xp/assert');

// BEGIN
// Returns the profile of user1 for myapp
var profile = authLib.getProfile({
    key: "user:enonic:user1",
    scope: "myapp"
});
// END

// BEGIN
// Information when retrieving a profile.
var expected = {
    "myApp": {
        "subString": "subStringValue",
        "subLong": 123
    },
    "string": "stringValue"
};
// END

assert.assertJsonEquals(expected, profile);
