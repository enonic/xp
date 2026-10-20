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

exports.loginUnspecifiedIdProvider = function () {

    var result = auth.login({
        user: 'user1@enonic.com',
        password: 'pwd123'
    });

    var expectedJson = {
        authenticated: true,
        user: {
            'type': 'user',
            'key': 'user:system:user1',
            'displayName': 'User 1',
            'modifiedTime': '1970-01-01T00:00:00Z',
            'disabled': false,
            'email': 'user1@enonic.com',
            'login': 'user1',
            'idProvider': 'system',
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

exports.loginWithRoles = function () {
    var result = auth.login({
        user: 'user1',
        idProvider: 'enonic',
        skipAuth: true,
        scope: 'REQUEST',
        roles: ['role:cms.project.site.viewer', 'role:system.admin']
    });

    t.assertTrue(result.authenticated);
};

exports.loginWithActor = function () {
    var result = auth.login({
        user: 'user1',
        idProvider: 'enonic',
        skipAuth: true,
        scope: 'REQUEST',
        actor: 'user:enonic:automation'
    });

    t.assertTrue(result.authenticated);
};

exports.loginWithGroupInRoles = function () {
    auth.login({
        user: 'user1',
        password: 'password',
        idProvider: 'enonic',
        roles: ['role:cms.project.site.viewer', 'group:system:editors']
    });
};

exports.loginWithEmptyRoles = function () {
    var result = auth.login({
        user: 'user1',
        idProvider: 'enonic',
        skipAuth: true,
        scope: 'REQUEST',
        roles: []
    });

    t.assertTrue(result.authenticated);
};

exports.loginWithAccessToken = function () {
    var result = auth.login({
        user: 'user1',
        idProvider: 'enonic',
        skipAuth: true,
        scope: 'REQUEST',
        accessToken: {
            issuer: 'https://issuer.example',
            clientId: 'cli',
            scope: ['content:read', 'profile', 'unknown'],
            audience: ['xp-api']
        }
    });

    t.assertTrue(result.authenticated);
};

exports.loginWithAccessTokenAndRoles = function () {
    var result = auth.login({
        user: 'user1',
        idProvider: 'enonic',
        skipAuth: true,
        scope: 'REQUEST',
        roles: ['role:cms.project.site.viewer'],
        accessToken: {
            issuer: 'https://issuer.example',
            scope: ['content:read'],
            audience: ['xp-api']
        }
    });

    t.assertTrue(result.authenticated);
};

exports.loginWithAccessTokenForAnotherAudience = function () {
    var result = auth.login({
        user: 'user1',
        idProvider: 'enonic',
        skipAuth: true,
        scope: 'REQUEST',
        accessToken: {
            issuer: 'https://issuer.example',
            audience: ['someone-else']
        }
    });

    t.assertFalse(result.authenticated);
    t.assertEquals('Token audience not accepted here', result.message);
};

exports.loginWithAccessTokenForXp = function () {
    var result = auth.login({
        user: 'user1',
        idProvider: 'enonic',
        skipAuth: true,
        scope: 'REQUEST',
        accessToken: {
            issuer: 'https://issuer.example',
            clientId: 'cli',
            audience: ['xp']
        }
    });

    t.assertTrue(result.authenticated);
};

exports.loginWithAccessTokenWithoutAudience = function () {
    var result = auth.login({
        user: 'user1',
        idProvider: 'enonic',
        skipAuth: true,
        scope: 'REQUEST',
        accessToken: {
            issuer: 'https://issuer.example',
            clientId: 'cli'
        }
    });

    t.assertFalse(result.authenticated);
    t.assertEquals('Token audience not accepted here', result.message);
};

exports.loginWithAccessTokenWithoutIssuer = function () {
    auth.login({
        user: 'user1',
        idProvider: 'enonic',
        skipAuth: true,
        scope: 'REQUEST',
        accessToken: {
            scope: ['content:read']
        }
    });
};
