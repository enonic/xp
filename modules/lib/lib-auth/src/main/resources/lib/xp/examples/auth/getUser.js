var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
// Returns the current loggedin user.
var user = authLib.getUser();

if (user) {
    log.info('User logged in: %s', user.displayName);
}
// END

// BEGIN
// Information when retrieving a user.
var expected = {
    'type': 'user',
    'key': 'user:enonic:user1',
    'displayName': 'User 1',
    'modifiedTime': '1970-01-01T00:00:00Z',
    'disabled': false,
    'email': 'user1@enonic.com',
    'login': 'user1',
    'idProvider': 'enonic',
    'hasPassword': false
};
// END

t.assertJsonEquals(expected, user);
