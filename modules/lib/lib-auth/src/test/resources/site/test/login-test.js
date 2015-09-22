var assert = require('/lib/xp/assert.js');
var auth = require('/lib/xp/auth.js');

exports.loginSuccess = function () {

    var result = auth.login({
        user: 'user1@enonic.com',
        password: 'pwd123',
        userStore: 'enonic'
    });

    var expectedJson = {
        authenticated: true,
        user: {
            disabled: false,
            displayName: 'User 1',
            email: 'user1@enonic.com',
            key: 'user:enonic:user1',
            login: 'user1',
            modifiedTime: '1970-01-01T00:00:00Z'
        }
    };

    assert.assertJsonEquals('Login result not equals', expectedJson, result);

};

exports.loginNoUserStore = function () {

    var result = auth.login({
        user: 'user1@enonic.com',
        password: 'pwd123'
    });

    var expectedJson = {
        authenticated: true,
        user: {
            disabled: false,
            displayName: 'User 1',
            email: 'user1@enonic.com',
            key: 'user:enonic:user1',
            login: 'user1',
            userStore: "enonic",
            modifiedTime: '1970-01-01T00:00:00Z'
        }
    };

    assert.assertJsonEquals('Login result not equals', expectedJson, result);

};

exports.loginMultipleUserStore = function () {

    var result = auth.login({
        user: 'user1@enonic.com',
        password: 'pwd123',
        userStore: ['enonic', 'system']
    });

    var expectedJson = {
        authenticated: true,
        user: {
            disabled: false,
            displayName: 'User 1',
            email: 'user1@enonic.com',
            key: 'user:enonic:user1',
            login: 'user1',
            userStore: "enonic",
            modifiedTime: '1970-01-01T00:00:00Z'
        }
    };

    assert.assertJsonEquals('Login result not equals', expectedJson, result);

};

exports.invalidLogin = function () {

    var result = auth.login({
        user: 'user1@enonic.com',
        password: 'pwd123',
        userStore: 'enonic'
    });

    var expectedJson = {
        authenticated: false,
        message: 'Access Denied'
    };

    assert.assertJsonEquals('Login result not equals', expectedJson, result);

};