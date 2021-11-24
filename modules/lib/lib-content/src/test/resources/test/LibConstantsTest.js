var assert = require('/lib/xp/testing.js');
var contentLib = require('/lib/xp/content.js');

exports.archiveRootPath = function () {
    var result = contentLib.ARCHIVE_ROOT_PATH;
    assert.assertEquals("/archive", result.toString());
};

exports.contentRootPath = function () {
    var result = contentLib.CONTENT_ROOT_PATH;
    assert.assertEquals("/content", result.toString());
};
