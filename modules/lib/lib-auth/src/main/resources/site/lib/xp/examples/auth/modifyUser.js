var authLib = require('/lib/xp/auth');
var assert = require('/lib/xp/assert');

// BEGIN
// Callback to edit the user.
function editor(c) {
    c.displayName = 'Modified display name';
    c.email = "new_email@enonic.com";
    return c;
}

// Modify user with specified key.
var user = authLib.modifyUser({
    key: 'user:enonic:userId',
    editor: editor
});
// END

// BEGIN
// Information about the modified user.
var expected = {
    "type": "user",
    "key": "user:enonic:user1",
    "displayName": "Modified display name",
    "modifiedTime": "1970-01-01T00:00:00Z",
    "disabled": false,
    "email": "new_email@enonic.com",
    "login": "user1",
    "userStore": "enonic"
};
// END

assert.assertJsonEquals(expected, user);
