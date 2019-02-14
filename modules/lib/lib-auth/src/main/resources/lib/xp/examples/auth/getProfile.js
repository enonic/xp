var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Returns the profile of user1 for myapp
var profile = authLib.getProfile({
    key: "user:enonic:user1"
});
// Information when retrieving a profile.
var expectedProfile = {
    "myApp": {
        "subString": "subStringValue",
        "subLong": 123
    },
    "string": "stringValue"
};
// END

// BEGIN
// Returns the profile of user1 for myapp
var scopedProfile = authLib.getProfile({
    key: "user:enonic:user1",
    scope: "myApp"
});
// Information when retrieving a profile.
var expectedScopedProfile = {
    "subString": "subStringValue",
    "subLong": 123
};
// END

t.assertJsonEquals(expectedProfile, profile);
t.assertJsonEquals(expectedScopedProfile, scopedProfile);
