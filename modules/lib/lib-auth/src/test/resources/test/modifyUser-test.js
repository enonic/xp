var t = require('/lib/xp/testing.js');
var auth = require('/lib/xp/auth.js');

function editor(c) {
    c.displayName = 'Modified display name';
    c.email = 'modified_email@enonic.com';

    return c;
}

exports.modifyUser = function () {

    var result = auth.modifyUser({
        key: 'user:myIdProvider:userId',
        editor: editor
    });

    var expectedJson = {
        'type': 'user',
        'key': 'user:enonic:user1',
        'displayName': 'Modified display name',
        'modifiedTime': '1970-01-01T00:00:00Z',
        'disabled': false,
        'email': 'modified_email@enonic.com',
        'login': 'user1',
        'idProvider': 'enonic',
        'hasPassword': false
    };

    t.assertJsonEquals(expectedJson, result);

};
