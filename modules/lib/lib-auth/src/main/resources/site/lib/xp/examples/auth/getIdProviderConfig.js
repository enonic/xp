var authLib = require('/lib/xp/auth');
var t = require('/lib/xp/testing');

// BEGIN
var result = authLib.getIdProviderConfig();
log.info('Field value for the current ID provider config = %s', result.Field);
// END

// BEGIN
// ID Provider config returned.
var expected = {
    title: 'App Title',
    avatar: true,
    forgotPassword: {
        email: "noreply@example.com",
        site: "MyWebsite"
    }
};
// END

t.assertJsonEquals(expected, result);
