var contentLib = require('/lib/xp/content');
var t = require('/lib/xp/testing');

// BEGIN
// Get Config of Site in path
var result = contentLib.getSiteConfig({
    key: '/path/to/mycontent',
    applicationKey: app.name
});
log.info('Field value for the site config = %s', result.Field);
// END

// BEGIN
// Get Config of Site for Content id
var resultById = contentLib.getSiteConfig({
    key: '100124',
    applicationKey: app.name
});
// END

// BEGIN
// Site config returned.
var expected = {
    'Field': 42
};
// END

t.assertJsonEquals(expected, result);
