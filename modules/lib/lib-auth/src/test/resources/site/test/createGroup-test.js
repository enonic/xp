var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.createGroup = function () {

    var result = auth.createGroup({
        userStore: 'myUserStore',
        name: 'groupId',
        displayName: 'group display name',
        description: "description"
    });

    var expectedJson = {
        "type": "group",
        "key": "group:system:group-a",
        "displayName": "Group A",
        "modifiedTime": "1970-01-01T00:00:00Z",
        "description": "description"
    };

    assert.assertJsonEquals('createGroup result not equals', expectedJson, result);

};

exports.createGroupUnAuthenticated = function () {

    var result = auth.createGroup({
        userStore: 'myUserStore',
        name: 'groupId',
        description: "description"
    });

    var expectedJson = null;

    assert.assertJsonEquals('createGroupUnAuthenticated result not equals', expectedJson, result);
};