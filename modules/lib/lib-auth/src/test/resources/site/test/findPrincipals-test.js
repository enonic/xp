var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.findPrincipalsDefaultParameters = function () {

    var result = auth.findPrincipals({});

    var expectedJson = {
        "total": 3,
        "count": 3,
        "hits": [
            {
                "type": "group",
                "key": "group:system:group-a",
                "displayName": "Group A",
                "modifiedTime": "1970-01-01T00:00:00Z",
                "description": "description"
            },
            {
                "type": "role",
                "key": "role:aRole",
                "displayName": "Role Display Name",
                "modifiedTime": "1970-01-01T00:00:00Z",
                "description": "description"
            },
            {
                "type": "user",
                "key": "user:enonic:user1",
                "displayName": "User 1",
                "modifiedTime": "1970-01-01T00:00:00Z",
                "disabled": false,
                "email": "user1@enonic.com",
                "login": "user1",
                "userStore": "enonic"
            }
        ]
    };

    assert.assertJsonEquals('findPrincipals result not equals', expectedJson, result);

};

exports.findPrincipalsUsers = function () {

    var result = auth.findPrincipals({
        "type": "user",
        "userStore": "enonic",
        "start": 2,
        "count": 3
    });

    var expectedJson = {
        "total": 3,
        "count": 1,
        "hits": [
            {
                "type": "user",
                "key": "user:enonic:user1",
                "displayName": "User 1",
                "modifiedTime": "1970-01-01T00:00:00Z",
                "disabled": false,
                "email": "user1@enonic.com",
                "login": "user1",
                "userStore": "enonic"
            }
        ]
    };

    assert.assertJsonEquals('findPrincipals result not equals', expectedJson, result);

};

exports.findPrincipalsGroups = function () {

    var result = auth.findPrincipals({
        "type": "group",
        "userStore": "enonic",
        "start": 2,
        "count": 3
    });

    var expectedJson = {
        "total": 3,
        "count": 1,
        "hits": [
            {
                "type": "group",
                "key": "group:system:group-a",
                "displayName": "Group A",
                "modifiedTime": "1970-01-01T00:00:00Z",
                "description": "description"
            }
        ]
    };

    assert.assertJsonEquals('findPrincipals result not equals', expectedJson, result);

};

exports.findPrincipalsRoles = function () {

    var result = auth.findPrincipals({
        "type": "role",
        "userStore": "enonic",
        "start": 2,
        "count": 3
    });

    var expectedJson = {
        "total": 3,
        "count": 1,
        "hits": [
            {
                "type": "role",
                "key": "role:aRole",
                "displayName": "Role Display Name",
                "modifiedTime": "1970-01-01T00:00:00Z",
                "description": "description"
            }
        ]
    };

    assert.assertJsonEquals('findPrincipals result not equals', expectedJson, result);

};

exports.findPrincipalsByName = function () {

    var result = auth.findPrincipals({
        "userStore": "enonic",
        "name": "user1"
    });

    var expectedJson = {
        "total": 1,
        "count": 1,
        "hits": [
            {
                "type": "user",
                "key": "user:enonic:user1",
                "displayName": "User 1",
                "modifiedTime": "1970-01-01T00:00:00Z",
                "disabled": false,
                "email": "user1@enonic.com",
                "login": "user1",
                "userStore": "enonic"
            }
        ]
    };

    assert.assertJsonEquals('findPrincipals result not equals', expectedJson, result);

};

exports.findPrincipalsBySearchText = function () {

    var result = auth.findPrincipals({
        "searchText": "enonic"
    });

    var expectedJson = {
        "total": 1,
        "count": 1,
        "hits": [
            {
                "type": "user",
                "key": "user:enonic:user1",
                "displayName": "User 1",
                "modifiedTime": "1970-01-01T00:00:00Z",
                "disabled": false,
                "email": "user1@enonic.com",
                "login": "user1",
                "userStore": "enonic"
            }
        ]
    };

    assert.assertJsonEquals('findPrincipals result not equals', expectedJson, result);

};