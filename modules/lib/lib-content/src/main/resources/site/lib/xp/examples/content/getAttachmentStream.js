var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Get stream for attachment.
var stream = contentLib.getAttachmentStream({
    key: '/a/b/mycontent',
    name: 'document.pdf'
});
// END

assert.assertNotNull(stream);
