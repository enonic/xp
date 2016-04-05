var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

function editor(c) {
    c.displayName = 'Modified display name';
    c.description = 'descriptionY';
    return c;
}

exports.modifyGroup = function () {

    var result = auth.modifyGroup({
        key: 'group:myGroupStore:groupId',
        editor: editor
    });

    var expectedJson = {
        "type": "group",
        "key": "group:system:group-a",
        "displayName": "Modified display name",
        "modifiedTime": "1970-01-01T00:00:00Z",
        "description": "descriptionY"
    };

    assert.assertJsonEquals('modifyUser result not equals', expectedJson, result);

};