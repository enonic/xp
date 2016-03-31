var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.getUserPrincipal = function () {

    var result = auth.getPrincipal('user:myUserStore:userId');

    var expectedJson = {
        "type": "user",
        "key": "user:enonic:user1",
        "displayName": "User 1",
        "modifiedTime": "1970-01-01T00:00:00Z",
        "disabled": false,
        "email": "user1@enonic.com",
        "login": "user1",
        "userStore": "enonic"
    };

    assert.assertJsonEquals('getPrincipal result not equals', expectedJson, result);

};

exports.getRolePrincipal = function () {

    var result = auth.getPrincipal('role:roleId');

    var expectedJson = {
        "type": "role",
        "key": "role:aRole",
        "displayName": "Role Display Name",
        "modifiedTime": "1970-01-01T00:00:00Z",
        "description": "description"
    };

    assert.assertJsonEquals('getRole result not equals', expectedJson, result);

};

exports.getGroupPrincipal = function () {

    var result = auth.getPrincipal('group:myGroupStore:groupId');

    var expectedJson = {
        "type": "group",
        "key": "group:system:group-a",
        "displayName": "Group A",
        "modifiedTime": "1970-01-01T00:00:00Z",
        "description": "description"
    };

    assert.assertJsonEquals('getRole result not equals', expectedJson, result);

};

exports.getNonExistingPrincipal = function () {

    var result = auth.getPrincipal('user:myUserStore:XXX');

    assert.assertEquals('getPrincipal result not null', null, result);

};