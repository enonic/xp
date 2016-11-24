var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/assert');

// BEGIN
// Unpublish content by path or key
var result = contentLib.unpublish({
    keys: ['/mysite/somepage', '79e21db0-5b43-45ce-b58c-6e1c420b22bd']
});

log.info('Unpublished content ids: ' + result.join(','));
// END

// BEGIN
// Content unpublished.
var expected = [
    "d7ad428b-eae2-4ff1-9427-e8e8a8a3ab23",
    "9f5b0db0-38f9-4e81-b92e-116f25476b1c",
    "e1f57280-d672-4cd8-b674-98e26e5b69ae"
];
// END

assert.assertJsonEquals(expected, result);
