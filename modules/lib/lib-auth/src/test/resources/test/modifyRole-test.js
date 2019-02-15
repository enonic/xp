var t = require('/lib/xp/testing.js');
var libAuth = require('/lib/xp/auth.js');

function editor(c) {
    c.displayName = 'Modified display name';
    c.description = 'descriptionY';
    return c;
}

exports.modifyRole = function () {

    var result = libAuth.modifyRole({
        key: 'role:aRole',
        editor: editor
    });

    var expectedJson = {
        'type': 'role',
        'key': 'role:aRole',
        'displayName': 'Modified display name',
        'modifiedTime': '1970-01-01T00:00:00Z',
        'description': 'descriptionY'
    };

    t.assertJsonEquals(expectedJson, result);

};
