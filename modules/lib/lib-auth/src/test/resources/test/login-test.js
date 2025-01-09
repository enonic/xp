var t = require('/lib/xp/testing.js');
var auth = require('/lib/xp/auth.js');

exports.loginSuccess = function () {

    var result = auth.login({
        user: 'user1@enonic.com',
        password: 'pwd123',
        idProvider: 'enonic'
    });

    var expectedJson = {
        authenticated: true,
        user: {
            'type': 'user',
            'key': 'user:enonic:user1',
            'displayName': 'User 1',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'disabled': false,
            'email': 'user1@enonic.com',
            'login': 'user1',
            'idProvider': 'enonic',
            'hasPassword': false
        }
    };

    t.assertJsonEquals(expectedJson, result);

};

exports.loginSuccessNoSession = function () {

    var result = auth.login({
        user: 'user1@enonic.com',
        password: 'pwd123',
        idProvider: 'enonic',
        scope: 'REQUEST'
    });

    var expectedJson = {
        authenticated: true,
        user: {
            'type': 'user',
            'key': 'user:enonic:user1',
            'displayName': 'User 1',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'disabled': false,
            'email': 'user1@enonic.com',
            'login': 'user1',
            'idProvider': 'enonic',
            'hasPassword': false
        }
    };

    t.assertJsonEquals(expectedJson, result);

};

exports.invalidLogin = function () {

    var result = auth.login({
        user: 'user1@enonic.com',
        password: 'pwd123',
        idProvider: 'enonic'
    });

    var expectedJson = {
        authenticated: false,
        message: 'Access Denied'
    };

    t.assertJsonEquals(expectedJson, result);

};

exports.loginNoIdProvider = function () {

    var result = auth.login({
        user: 'user1@enonic.com',
        password: 'pwd123'
    });

    var expectedJson = {
        authenticated: true,
        user: {
            'type': 'user',
            'key': 'user:enonic:user1',
            'displayName': 'User 1',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'disabled': false,
            'email': 'user1@enonic.com',
            'login': 'user1',
            'idProvider': 'enonic',
            'hasPassword': false
        }
    };

    t.assertJsonEquals(expectedJson, result);

};

exports.loginMultipleIdProvider = function () {

    var result = auth.login({
        user: 'user1@enonic.com',
        password: 'pwd123',
        idProvider: ['enonic', 'system']
    });

    var expectedJson = {
        authenticated: true,
        user: {
            'type': 'user',
            'key': 'user:enonic:user1',
            'displayName': 'User 1',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'disabled': false,
            'email': 'user1@enonic.com',
            'login': 'user1',
            'idProvider': 'enonic',
            'hasPassword': false
        }
    };

    t.assertJsonEquals(expectedJson, result);

};

exports.loginUnspecifiedIdProvider = function () {

    var result = auth.login({
        user: 'user1@enonic.com',
        password: 'pwd123'
    });

    var expectedJson = {
        authenticated: true,
        user: {
            'type': 'user',
            'key': 'user:enonic:user1',
            'displayName': 'User 1',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'disabled': false,
            'email': 'user1@enonic.com',
            'login': 'user1',
            'idProvider': 'enonic',
            'hasPassword': false
        }
    };

    t.assertJsonEquals(expectedJson, result);

};

exports.loginWithScopeNONE = function () {
    var result = auth.login({
        user: 'user1@enonic.com',
        password: 'pwd123',
        scope: 'NONE'
    });

    var expectedJson = {
        authenticated: true,
        user: {
            'type': 'user',
            'key': 'user:enonic:user1',
            'displayName': 'User 1',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'disabled': false,
            'email': 'user1@enonic.com',
            'login': 'user1',
            'idProvider': 'enonic',
            'hasPassword': false
        }
    };

    t.assertJsonEquals(expectedJson, result);
};

exports.loginWithSkipAuth = function () {

    var result = auth.login({
        user: 'user1@enonic.com',
        skipAuth: true,
        idProvider: 'enonic'
    });

    var expectedJson = {
        authenticated: true,
        user: {
            'type': 'user',
            'key': 'user:enonic:user1',
            'displayName': 'User 1',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'disabled': false,
            'email': 'user1@enonic.com',
            'login': 'user1',
            'idProvider': 'enonic',
            'hasPassword': false
        }
    };

    t.assertJsonEquals(expectedJson, result);

};

