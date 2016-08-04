var authLib = require('/lib/xp/auth');
var assert = require('/lib/xp/assert');

// BEGIN
// Callback to edit the user.
function editor(c) {
    if (!c) {
        c = {};
    }
    c.myfield = "Modified field";
    return c;
}

// Modify user extra data
var userExtraData = authLib.modifyUserExtraData({
    key: "user:enonic:user1",
    namespace: "com.enonic.app.myapp",
    editor: editor
});
// END

// BEGIN
// Information about the modified user.
var expected = {
    "set": {
        "subString": "subStringValue",
        "subLong": 123
    },
    "string": "stringValue",
    "myfield": "Modified field"
};
// END

assert.assertJsonEquals(expected, userExtraData);
