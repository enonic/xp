var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// BEGIN
// Gets all attachments for a content.
var result = contentLib.getAttachments('/features/js-libraries/mycontent');

if (!result) {
    log.info('Content was not found');
} else {
    log.info('Attachments as JSON %s', result);
}
// END

// BEGIN
// Attachments returned.
var expected = {
    "logo.png": {
        "name": "logo.png",
        "label": "small",
        "size": 6789,
        "mimeType": "image/png"
    },
    "document.pdf": {
        "name": "document.pdf",
        "size": 12345,
        "mimeType": "application/pdf"
    }
};
// END

assert.assertJsonEquals(expected, result);
