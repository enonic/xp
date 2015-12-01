var assert = require('/lib/xp/assert.js');
var content = require('/lib/xp/content.js');

var expectedJson = {
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

exports.getById = function () {
    var result = content.getAttachments('123456');

    assert.assertJsonEquals(expectedJson, result);
};

exports.getByPath = function () {
    var result = content.getAttachments('/a/b/mycontent');

    assert.assertJsonEquals(expectedJson, result);
};

exports.getById_notFound = function () {
    var result = content.getAttachments('123456');

    assert.assertNull(result);
};

exports.getByPath_notFound = function () {
    var result = content.getAttachments('/a/b/mycontent');

    assert.assertNull(result);
};
