const contentLib = require('/lib/xp/content');
const assert = require('/lib/xp/testing');

// BEGIN
// Deletes a content by path.
var result = contentLib.deleteContent({
    key: '/features/js-libraries/mycontent'
});

if (result) {
    log.info('Content deleted');
} else {
    log.info('Content was not found');
}
// END

assert.assertTrue(result);
