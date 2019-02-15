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
    "subString": "subStringValue",
    "subLong": 123,
    "newField": "New field"
};
// END

t.assertJsonEquals(expected, profile);
