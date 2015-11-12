var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.getUserAuthenticated = function () {

    var result = auth.getUser();

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

    assert.assertJsonEquals('getUser result not equals', expectedJson, result);

};

exports.getUserNotAuthenticated = function () {

    var result = auth.getUser();

    assert.assertEquals('getUser result not null', null, result);

};