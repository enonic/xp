var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Creates a group.
var group = authLib.createGroup({
    idProvider: 'myIdProvider',
    name: 'groupName',
    displayName: 'Group display name',
    description: 'description'
});
// END

// BEGIN
// Information about the created group.
var expected = {
    'type': 'group',
    'key': 'group:system:group-a',
    'displayName': 'Group A',
    'modifiedTime': '1970-01-01T00:00:00Z',
    'description': 'description'
};
// END

t.assertJsonEquals(expected, group);
