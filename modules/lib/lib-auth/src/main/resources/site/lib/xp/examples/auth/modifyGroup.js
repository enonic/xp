var authLib = require('/lib/xp/auth');
var assert = require('/lib/xp/assert');

// BEGIN
// Callback to edit the group.
function editor(c) {
    c.displayName = 'Modified display name';
    c.description = 'descriptionX'
    return c;
}

// Modify group with specified key.
var group = authLib.modifyGroup({
    key: 'group:enonic:groupId',
    editor: editor
});
// END

// BEGIN
// Information about the modified group.
var expected = {
    "type": "group",
    "key": "group:system:group-a",
    "displayName": "Modified display name",
    "modifiedTime": "1970-01-01T00:00:00Z",
    "description": "descriptionX"
};
// END

assert.assertJsonEquals(expected, group);
