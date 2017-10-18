var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Callback to edit the role.
function editor(c) {
    c.displayName = 'Modified display name';
    c.description = 'descriptionX';
    return c;
}

// Modify role with specified key.
var role = authLib.modifyRole({
    key: 'role:aRole',
    editor: editor
});
// END

// BEGIN
// Information about the modified role.
var expected = {
    'type': 'role',
    'key': 'role:aRole',
    'displayName': 'Modified display name',
    'modifiedTime': '1970-01-01T00:00:00Z',
    'description': 'descriptionX'
};
// END

t.assertJsonEquals(expected, role);
