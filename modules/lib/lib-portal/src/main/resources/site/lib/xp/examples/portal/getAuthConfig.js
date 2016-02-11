var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/assert');

// BEGIN
var result = portalLib.getAuthConfig();
log.info('Field value for the current auth config = %s', result.Field);
// END

// BEGIN
// Site config returned.
var expected = {
    "Field": 42
};
// END

assert.assertJsonEquals(expected, result);
