var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN 
// Login with a explicit user store.
var result1 = authLib.login({
    user: 'user1@enonic.com',
    password: 'secret',
    userStore: 'enonic'
});

if (result1.authenticated) {
    log.info('User logged in: %s', result1.user.displayName);
}
// END

// BEGIN
// Login to any of the user stores, in sequence.
var result2 = authLib.login({
    user: 'user1@enonic.com',
    password: 'secret',
    userStore: ['enonic', 'vip']
});
// END

// BEGIN
// Login to any of the existing user stores.
var result3 = authLib.login({
    user: 'user1@enonic.com',
    password: 'secret'
});
// END

// BEGIN
// Login with a explicit user store without authentication.
var result4 = authLib.login({
    user: 'user1@enonic.com',
    userStore: 'enonic',
    skipAuth: true
});
// END

// BEGIN
// Result of a successful login operation.
var expected = {
    "authenticated": true,
    "user": {
        "type": "user",
        "key": "user:enonic:user1",
        "displayName": "User 1",
        "modifiedTime": "1970-01-01T00:00:00Z",
        "disabled": false,
        "email": "user1@enonic.com",
        "login": "user1",
        "userStore": "enonic"
    }
};
// END

t.assertJsonEquals(expected, result1);
t.assertJsonEquals(expected, result2);
t.assertJsonEquals(expected, result3);
t.assertJsonEquals(expected, result4);
