var authLib = require('/lib/xp/auth');
var assert = require('/lib/xp/assert');

// BEGIN
// Callback to edit the user profile.
function editor(c) {
    if (!c) {
        c = {};
    }
    c.myfield = "Modified field";
    return c;
}

// Modify the profile of user1 for myapp
var profile = authLib.modifyProfile({
    key: "user:enonic:user1",
    scope: "myapp",
    editor: editor
});
// END

// BEGIN
// Information about the modified profile.
var expected = {
    "myApp": {
        "subString": "subStringValue",
        "subLong": 123
    },
    "string": "stringValue",
    "myfield": "Modified field"
};
// END

assert.assertJsonEquals(expected, profile);
