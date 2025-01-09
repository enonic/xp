var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN 
// Login with a explicit id provider.
var result1 = authLib.login({
    user: 'user1@enonic.com',
    password: 'secret',
    idProvider: 'enonic'
});

if (result1.authenticated) {
    log.info('User logged in: %s', result1.user.displayName);
}
// END

// BEGIN
// Login to any of the id providers, in sequence.
var result2 = authLib.login({
    user: 'user1@enonic.com',
    password: 'secret',
    idProvider: ['enonic', 'vip']
});
// END

// BEGIN
// Login to any of the existing id providers.
var result3 = authLib.login({
    user: 'user1@enonic.com',
    password: 'secret'
});
// END

// BEGIN
// Login with a explicit id provider without authentication.
var result4 = authLib.login({
    user: 'user1@enonic.com',
    idProvider: 'enonic',
    skipAuth: true
});
// END

// BEGIN
// Login with an explicit scope SESSION.
var result5 = authLib.login({
    user: 'user1@enonic.com',
    idProvider: 'enonic',
    skipAuth: true,
    scope: 'SESSION'
});
// END

// BEGIN
// Login with an explicit scope REQUEST.
var result6 = authLib.login({
    user: 'user1@enonic.com',
    idProvider: 'enonic',
    skipAuth: true,
    scope: 'REQUEST'
});
// END

// BEGIN
// Result of a successful login operation.
var expected = {
    'authenticated': true,
    'user': {
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
// END

t.assertJsonEquals(expected, result1);
t.assertJsonEquals(expected, result2);
t.assertJsonEquals(expected, result3);
t.assertJsonEquals(expected, result4);
t.assertJsonEquals(expected, result5);
t.assertJsonEquals(expected, result6);
