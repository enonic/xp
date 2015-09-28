var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.getUserAuthenticated = function () {

    var result = auth.getUser();

    var expectedJson = {
        disabled: false,
        displayName: 'User 1',
        email: 'user1@enonic.com',
        key: 'user:enonic:user1',
        login: 'user1',
        userStore: "enonic",
        modifiedTime: '1970-01-01T00:00:00Z'
    };

    assert.assertJsonEquals('getUser result not equals', expectedJson, result);

};

exports.getUserNotAuthenticated = function () {

    var result = auth.getUser();

    assert.assertEquals('getUser result not null', null, result);

};