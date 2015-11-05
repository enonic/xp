var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

function editor(c) {
    c.displayName = 'Modified display name';
    c.email = "Modified email";

    return c;
}

exports.modifyUser = function () {

    var result = auth.modifyUser({
        key: 'user:myUserStore:userId',
        editor: editor
    });

    var expectedJson = {
        "type": "user",
        "key": "user:enonic:user1",
        "displayName": "Modified display name",
        "modifiedTime": "1970-01-01T00:00:00Z",
        "disabled": false,
        "email": "Modified email",
        "login": "user1",
        "userStore": "enonic"
    };

    assert.assertJsonEquals('modifyUser result not equals', expectedJson, result);

};