var t = require('/lib/xp/testing.js');
var auth = require('/lib/xp/auth.js');

exports.getMembers = function () {

    var result = auth.getMembers({key:'group:system:group-a'});

    var expectedJson = [
        {
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
            "type": "user",
            "key": "user:enonic:user2",
            "displayName": "User 2",
            "modifiedTime": "1970-01-01T00:00:00Z",
            "disabled": false,
            "email": "user2@enonic.com",
            "login": "user2",
            "userStore": "enonic"
        }
    ];

    t.assertJsonEquals(expectedJson, result);

};

exports.getMembersWithFrom = function () {

    var result = auth.getMembers({key:'group:system:group-a', from:1});

    var expectedJson = [
        {
            "type": "user",
            "key": "user:enonic:user2",
            "displayName": "User 2",
            "modifiedTime": "1970-01-01T00:00:00Z",
            "disabled": false,
            "email": "user2@enonic.com",
            "login": "user2",
            "userStore": "enonic"
        }
    ];

    t.assertJsonEquals(expectedJson, result);

};

exports.getMembersWithSize = function () {

    var result = auth.getMembers({key:'group:system:group-a', size:1});

    var expectedJson = [
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
    ];

    t.assertJsonEquals(expectedJson, result);

};

exports.getNoMembers = function () {

    var result = auth.getMembers({key:'group:system:group-a'});

    var expectedJson = [];

    t.assertJsonEquals(expectedJson, result);

};