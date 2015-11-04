var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.createGroup = function () {

    var result = auth.createGroup('myUserStore', 'groupId', 'group display name');

    var expectedJson = {
        "type": "group",
        "key": "group:system:group-a",
        "displayName": "Group A",
        "modifiedTime": "1970-01-01T00:00:00Z"
    };

    assert.assertJsonEquals('createGroup result not equals', expectedJson, result);

};

exports.createGroupUnAuthenticated = function () {

    var result = auth.createGroup('myUserStore', 'groupId');

    var expectedJson = null;

    assert.assertJsonEquals('createGroupUnAuthenticated result not equals', expectedJson, result);
};