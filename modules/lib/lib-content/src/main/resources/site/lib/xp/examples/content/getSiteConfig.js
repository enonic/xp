var contentLib = require('/lib/xp/content');
var t = require('/lib/xp/testing');

// BEGIN
var result = contentLib.getSiteConfig({
    key: '/path/to/mycontent',
    applicationKey: app.name
});
log.info('Field value for the site config = %s', result.Field);
// END

// BEGIN
// Site config returned.
var expected = {
    "Field": 42
};
// END

t.assertJsonEquals(expected, result);
