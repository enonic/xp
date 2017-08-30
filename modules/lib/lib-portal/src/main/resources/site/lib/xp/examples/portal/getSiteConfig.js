var portalLib = require('/lib/xp/portal');
var assert = require('/lib/xp/testing');

// BEGIN
var result = portalLib.getSiteConfig();
log.info('Field value for the current site config = %s', result.Field);
// END

// BEGIN
// Site config returned.
var expected = {
    "Field": 42
};
// END

assert.assertJsonEquals(expected, result);
