var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.getUserMemberships = function () {

    var result = auth.getMemberships('user:myUserStore:userId');

    var expectedJson = [{
        "type": "user",
        "key": "user:enonic:user1",
        "displayName": "User 1",
        "modifiedTime": "1970-01-01T00:00:00Z",
        "disabled": false,
        "email": "user1@enonic.com",
        "login": "user1",
        "userStore": "enonic"
    }];

    assert.assertJsonEquals('getMemberships result not equals', expectedJson, result);

};

exports.getUserMembershipsWithRoleAndGroup = function () {

    var result = auth.getMemberships('user:myUserStore:userId');

    var expectedJson = [{
        "type": "user",
        "key": "user:enonic:user1",
        "displayName": "User 1",
        "modifiedTime": "1970-01-01T00:00:00Z",
        "disabled": false,
        "email": "user1@enonic.com",
        "login": "user1",
        "userStore": "enonic"
    },
        {
            "type": "role",
            "key": "role:aRole",
            "displayName": "Role Display Name",
            "modifiedTime": "1970-01-01T00:00:00Z"
        },
        {
            "type": "group",
            "key": "group:system:group-a",
            "displayName": "Group A",
            "modifiedTime": "1970-01-01T00:00:00Z"
        }];

    assert.assertJsonEquals('getMemberships result not equals', expectedJson, result);

};

exports.getNonExistingMemberships = function () {

    var result = auth.getMemberships('user:myUserStore:XXX');

    var expectedJson = [];

    assert.assertJsonEquals('getMemberships result not equals', expectedJson, result);

};