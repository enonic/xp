var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Callback to edit the user profile.
function editor(c) {
    if (!c) {
        c = {};
    }
    c.newField = "New field";
    return c;
}

// Modify the profile of user1 for myapp
var profile = authLib.modifyProfile({
    key: "user:enonic:user1",
    scope: "myApp",
    editor: editor
});
// END

// BEGIN
// Information about the modified profile.
var expected = {
    "untouchedString": "originalValue",
    "untouchedBoolean": true,
    "untouchedDouble": 2,
    "untouchedLong": 2,
    "untouchedLink": "myLink",
    "untouchedInstant": "2017-01-02T10:00:00Z",
    "untouchedBinaryRef": "abcd",
    "untouchedGeoPoint": "30.0,-30.0",
    "untouchedLocalDate": "2017-03-24",
    "untouchedReference": "myReference",
    "newField": "New field"
};
// END

t.assertJsonEquals(expected, profile);
